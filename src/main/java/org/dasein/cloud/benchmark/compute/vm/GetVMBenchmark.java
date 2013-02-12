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

import org.dasein.cloud.CloudProvider;
import org.dasein.cloud.benchmark.Benchmark;
import org.dasein.cloud.benchmark.BenchmarkExecution;
import org.dasein.cloud.benchmark.Milestone;
import org.dasein.cloud.compute.VirtualMachine;
import org.json.JSONObject;

import javax.annotation.Nonnull;

/**
 * [Class Documentation]
 * <p>Created by George Reese: 2/11/13 8:15 PM</p>
 *
 * @author George Reese
 */
public class GetVMBenchmark implements Benchmark {
    @Override
    public @Nonnull String getDescription() {
        return "Tests the fetching of a known virtual machine in a target cloud with baseline actionable data.";
    }

    @Override
    public @Nonnull String getName() {
        return "getVM";
    }

    @Override
    public @Nonnull BenchmarkExecution execute(@Nonnull CloudProvider provider, @Nonnull JSONObject cfg) {
        String description = "Tests the fetching of a known virtual machine in a target cloud with baseline actionable data.";
        String name = "getVM";
        long start = System.currentTimeMillis();

        try {
            //noinspection UnusedDeclaration,ConstantConditions
            provider.getComputeServices().getVirtualMachineSupport().getVirtualMachine(cfg.getString("virtualMachineId"));

            long end = System.currentTimeMillis();
            Milestone m = Milestone.getInstance(name, description, start, end, null);

            return BenchmarkExecution.getSuccess(this, provider, start, end, m);
        }
        catch( Throwable t ) {
            long end = System.currentTimeMillis();
            Milestone m = Milestone.getInstance(name, description, start, end, t);

            return BenchmarkExecution.getError(this, provider, start, end, m);
        }
    }
}
