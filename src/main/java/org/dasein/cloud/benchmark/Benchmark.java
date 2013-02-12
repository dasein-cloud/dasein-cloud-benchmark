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
import org.json.JSONObject;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

/**
 * [Class Documentation]
 * <p>Created by George Reese: 2/11/13 2:46 PM</p>
 *
 * @author George Reese
 */
public interface Benchmark {
    public @Nonnull String getDescription();

    public @Nonnull String getName();

    public @Nonnull BenchmarkExecution execute(@Nonnull CloudProvider provider, @Nonnull JSONObject cfg);
}
