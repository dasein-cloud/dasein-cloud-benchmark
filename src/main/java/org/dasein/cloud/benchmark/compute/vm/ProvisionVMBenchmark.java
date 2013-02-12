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

package org.dasein.cloud.benchmark.compute.vm;

import org.dasein.cloud.CloudException;
import org.dasein.cloud.CloudProvider;
import org.dasein.cloud.benchmark.Benchmark;
import org.dasein.cloud.benchmark.BenchmarkExecution;
import org.dasein.cloud.benchmark.Milestone;
import org.dasein.cloud.compute.VMLaunchOptions;
import org.dasein.cloud.compute.VirtualMachine;
import org.dasein.cloud.compute.VmState;
import org.json.JSONException;
import org.json.JSONObject;

import javax.annotation.Nonnull;

/**
 * [Class Documentation]
 * <p>Created by George Reese: 2/11/13 8:15 PM</p>
 *
 * @author George Reese
 */
public class ProvisionVMBenchmark implements Benchmark {
    @Override
    public @Nonnull String getDescription() {
        return "Tests the provisioning of a virtual machine from an image/template.";
    }

    @Override
    public @Nonnull String getName() {
        return "provisionVM";
    }

    @Override
    public @Nonnull BenchmarkExecution execute(@Nonnull CloudProvider provider, @Nonnull JSONObject cfg) {
        VMLaunchOptions options;

        try {
            String productId = cfg.getString("productId");
            String imageId = cfg.getString("imageId");
            String name = "benchmark-" + (System.currentTimeMillis()%10000);
            String az = cfg.getString("dataCenterId");

            options = VMLaunchOptions.getInstance(productId, imageId, name, name);

            options.inDataCenter(az);
            if( cfg.has("subnetId") ) {
                options.inVlan(null, az, cfg.getString("subnetId"));
            }
        }
        catch( JSONException e ) {
            throw new RuntimeException("Configuration error: " + e.getMessage());
        }
        long start = System.currentTimeMillis();

        String name = "Time to ID";
        String description = "Benchmarks the time it takes from the point of request to the point at which we have an actionable VM ID to track";

        try {
            VirtualMachine vm = null;

            try {
                vm = provider.getComputeServices().getVirtualMachineSupport().launch(options);
                long ts = System.currentTimeMillis();
                Milestone launch, done;

                launch = Milestone.getInstance(name, description, start, ts, null);
                name = "Time to Running";
                description = "Benchmarks the time it takes for a virtual machine to reach a running state";
                try {
                    while( !vm.getCurrentState().equals(VmState.RUNNING) ) {
                        try { Thread.sleep(1000L); }
                        catch( InterruptedException ignore ) { }
                        try { //noinspection ConstantConditions
                            vm = provider.getComputeServices().getVirtualMachineSupport().getVirtualMachine(vm.getProviderVirtualMachineId());
                        }
                        catch( Throwable ignore ) {
                            // ignore
                        }
                        if( vm == null ) {
                            throw new CloudException("VM went away");
                        }
                    }
                    long end = System.currentTimeMillis();

                    done = Milestone.getInstance(name, description, ts, end, null);
                    return BenchmarkExecution.getSuccess(this, provider, start, end, launch, done);
                }
                catch( Throwable t ) {
                    long end = System.currentTimeMillis();

                    done = Milestone.getInstance(name, description, ts, end, t);
                    return BenchmarkExecution.getError(this, provider, start, end, launch, done);
                }
            }
            finally {
                if( vm != null ) {
                    try {
                        provider.getComputeServices().getVirtualMachineSupport().terminate(vm.getProviderVirtualMachineId());
                    }
                    catch( Throwable ignore ) {
                        // ignore
                    }
                }
            }
        }
        catch( Throwable t ) {
            long end= System.currentTimeMillis();
            Milestone m = Milestone.getInstance(name, description, start, end, t);

            return BenchmarkExecution.getError(this, provider, start, end, m);
        }
    }
}
