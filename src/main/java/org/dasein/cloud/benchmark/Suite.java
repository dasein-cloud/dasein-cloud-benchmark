/*
 * Copyright (C) 2013 enStratus Networks Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dasein.cloud.benchmark;

import org.dasein.cloud.CloudProvider;
import org.dasein.cloud.ProviderContext;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * [Class Documentation]
 * <p>Created by George Reese: 2/11/13 2:45 PM</p>
 *
 * @author George Reese
 */
public class Suite {
    static public void main(String ... args) throws Exception {
        ArrayList<Map<String,Object>> suites = new ArrayList<Map<String, Object>>();

        for( String suiteFile : args ) {
            HashMap<String,Object> suite = new HashMap<String, Object>();

            ArrayList<Benchmark> benchmarks = new ArrayList<Benchmark>();

            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(suiteFile)));
            StringBuilder json = new StringBuilder();
            String line;

            while( (line = reader.readLine()) != null ) {
                json.append(line);
                json.append("\n");
            }
            JSONObject ob = new JSONObject(json.toString());

            suite.put("name", ob.getString("name"));
            suite.put("description", ob.getString("description"));

            JSONArray benchmarkClasses = ob.getJSONArray("benchmarks");

            for( int i=0; i<benchmarkClasses.length(); i++ ) {
                String cname = benchmarkClasses.getString(i);

                benchmarks.add((Benchmark)Class.forName(cname).newInstance());
            }

            ArrayList<Map<String,Object>> tests = new ArrayList<Map<String, Object>>();
            JSONArray clouds = ob.getJSONArray("clouds");

            for( int i=0; i<clouds.length(); i++ ) {
                JSONObject cloud = clouds.getJSONObject(i);
                String cname = cloud.getString("providerClass");
                CloudProvider provider = (CloudProvider)Class.forName(cname).newInstance();
                JSONObject ctxCfg = cloud.getJSONObject("context");
                ProviderContext ctx = new ProviderContext();

                ctx.setAccountNumber(ctxCfg.getString("accountNumber"));
                ctx.setRegionId(ctxCfg.getString("bootstrapRegionId"));
                if( ctxCfg.has("accessPublic") ) {
                    ctx.setAccessPublic(ctxCfg.getString("accessPublic").getBytes("utf-8"));
                }
                if( ctxCfg.has("accessPrivate") ) {
                    ctx.setAccessPrivate(ctxCfg.getString("accessPrivate").getBytes("utf-8"));
                }
                ctx.setCloudName(ctxCfg.getString("cloudName"));
                ctx.setProviderName(ctxCfg.getString("providerName"));
                ctx.setEndpoint(ctxCfg.getString("endpoint"));
                if( ctxCfg.has("x509Cert") ) {
                    ctx.setX509Cert(ctxCfg.getString("x509Cert").getBytes("utf-8"));
                }
                if( ctxCfg.has("x509Key") ) {
                    ctx.setX509Key(ctxCfg.getString("x509Key").getBytes("utf-8"));
                }
                if( ctxCfg.has("customProperties") ) {
                    JSONObject p = ctxCfg.getJSONObject("customProperties");
                    String[] names = JSONObject.getNames(p);

                    if( names != null ) {
                        Properties props = new Properties();

                        for( String name : names ) {
                            String value = p.getString(name);

                            if( value != null ) {
                                props.put(name, value);
                            }
                        }
                        ctx.setCustomProperties(props);
                    }
                }
                provider.connect(ctx);

                Suite s = new Suite(benchmarks, provider);

                tests.add(s.runBenchmarks());
            }
            suite.put("benchmarks", tests);
            suites.add(suite);
        }
        System.out.println((new JSONArray(suites)).toString());
    }

    private List<Benchmark> benchmarks;
    private CloudProvider   provider;

    private Suite(@Nonnull List<Benchmark> benchmarks, @Nonnull CloudProvider provider) {
        this.benchmarks = benchmarks;
        this.provider = provider;
    }

    public Map<String,Object> runBenchmarks() throws IOException {
        HashMap<String,Object> map = new HashMap<String, Object>();
        ProviderContext ctx = provider.getContext();

        if( ctx == null ) {
            return map;
        }
        try {
            ArrayList<Map<String,Object>> executions = new ArrayList<Map<String, Object>>();

            map.put("cloudName", ctx.getCloudName());
            map.put("providerName", ctx.getProviderName());
            map.put("endpoint", ctx.getEndpoint());

            for( Benchmark benchmark : benchmarks ) {
                executions.add(toJSON(benchmark.execute(provider)));
            }
            map.put("benchmarks", executions);
        }
        finally {
            provider.close();
        }
        return map;
    }

    private @Nonnull Map<String,Object> toJSON(@Nonnull BenchmarkExecution execution) {
        HashMap<String,Object> json = new HashMap<String, Object>();

        json.put("duration", execution.getDuration().toString());
        json.put("start", (new Date(execution.getStartTimestamp())).toString());
        json.put("end", (new Date(execution.getEndTimestamp())).toString());
        json.put("success", execution.isSuccessful());

        HashMap<String,Object> b = new HashMap<String, Object>();
        Benchmark benchmark = execution.getBenchmark();

        b.put("name", benchmark.getName());
        b.put("description", benchmark.getDescription());

        json.put("benchmark", b);

        ArrayList<Map<String,Object>> milestones = new ArrayList<Map<String, Object>>();

        for( Milestone m : execution.getMilestones() ) {
            HashMap<String,Object> milestone = new HashMap<String, Object>();

            milestone.put("name", m.getName());
            milestone.put("description", m.getDescription());
            milestone.put("duration", m.getDuration().toString());
            milestone.put("start", (new Date(m.getStartTimestamp())).toString());
            milestone.put("end", (new Date(m.getEndTimestamp())).toString());
            Throwable t = m.getError();

            if( t != null ) {
                milestone.put("error", toJSON(t));
            }
            milestones.add(milestone);
        }

        json.put("milestones", milestones);
        return json;
    }

    private @Nonnull Map<String,Object> toJSON(@Nonnull Throwable t) {
        HashMap<String,Object> error = new HashMap<String, Object>();

        error.put("type", t.getClass().getName());
        error.put("message", t.getMessage());
        if( t.getCause() != null ) {
            error.put("cause", toJSON(t.getCause()));
        }
        return error;
    }
}
