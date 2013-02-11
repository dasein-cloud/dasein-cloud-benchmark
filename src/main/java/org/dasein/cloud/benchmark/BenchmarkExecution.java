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
import org.dasein.util.uom.time.Millisecond;
import org.dasein.util.uom.time.TimePeriod;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;

/**
 * [Class Documentation]
 * <p>Created by George Reese: 2/11/13 3:00 PM</p>
 *
 * @author George Reese
 */
public class BenchmarkExecution {
    public @Nonnull BenchmarkExecution getError(@Nonnull Benchmark benchmark, @Nonnull CloudProvider provider, @Nonnegative long startTimestamp, @Nonnegative long endTimestamp, @Nonnull Milestone ... milestones) {
        BenchmarkExecution execution = new BenchmarkExecution();

        execution.benchmark = benchmark;
        execution.cloudName = provider.getCloudName();
        execution.providerName = provider.getProviderName();
        //noinspection ConstantConditions
        execution.endpoint = provider.getContext().getEndpoint();
        execution.duration = new TimePeriod<Millisecond>(endTimestamp - startTimestamp, TimePeriod.MILLISECOND);
        execution.startTimestamp = startTimestamp;
        execution.endTimestamp = endTimestamp;
        execution.milestones = new ArrayList<Milestone>();
        Collections.addAll(execution.milestones, milestones);
        execution.successful = false;
        return execution;
    }

    public @Nonnull BenchmarkExecution getSuccess(@Nonnull Benchmark benchmark, @Nonnull CloudProvider provider, @Nonnegative long startTimestamp, @Nonnegative long endTimestamp, @Nonnull Milestone ... milestones) {
        BenchmarkExecution execution = new BenchmarkExecution();

        execution.benchmark = benchmark;
        execution.cloudName = provider.getCloudName();
        execution.providerName = provider.getProviderName();
        //noinspection ConstantConditions
        execution.endpoint = provider.getContext().getEndpoint();
        execution.duration = new TimePeriod<Millisecond>(endTimestamp - startTimestamp, TimePeriod.MILLISECOND);
        execution.startTimestamp = startTimestamp;
        execution.endTimestamp = endTimestamp;
        execution.milestones = new ArrayList<Milestone>();
        Collections.addAll(execution.milestones, milestones);
        execution.successful = true;
        return execution;
    }

    private Benchmark               benchmark;
    private String                  cloudName;
    private TimePeriod<Millisecond> duration;
    private long                    endTimestamp;
    private String                  endpoint;
    private ArrayList<Milestone>    milestones;
    private String                  providerName;
    private long                    startTimestamp;
    private boolean                 successful;

    private BenchmarkExecution() {}

    public @Nonnull Benchmark getBenchmark() {
        return benchmark;
    }

    public @Nonnull String getCloudName() {
        return cloudName;
    }

    public @Nonnegative TimePeriod<Millisecond> getDuration() {
        return duration;
    }

    public @Nonnegative long getEndTimestamp() {
        return endTimestamp;
    }

    public @Nonnull String getEndpoint() {
        return endpoint;
    }

    public @Nonnull Iterable<Milestone> getMilestones() {
        return milestones;
    }

    public @Nonnull String getProviderName() {
        return providerName;
    }

    public @Nonnegative long getStartTimestamp() {
        return startTimestamp;
    }

    public boolean isSuccessful() {
        return successful;
    }
}
