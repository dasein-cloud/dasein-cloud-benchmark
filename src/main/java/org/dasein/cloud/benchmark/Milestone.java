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

import org.dasein.util.uom.time.Millisecond;
import org.dasein.util.uom.time.TimePeriod;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * [Class Documentation]
 * <p>Created by George Reese: 2/11/13 2:53 PM</p>
 *
 * @author George Reese
 */
public class Milestone {
    static public Milestone getInstance(@Nonnull String name, @Nonnull String description, @Nonnegative long startTimestamp, @Nonnegative long endTimestamp, @Nullable Throwable exception) {
        Milestone milestone = new Milestone();

        milestone.name = name;
        milestone.description = description;
        milestone.startTimestamp = startTimestamp;
        milestone.endTimestamp = endTimestamp;
        milestone.duration = new TimePeriod<Millisecond>(endTimestamp - startTimestamp, TimePeriod.MILLISECOND);
        milestone.error = exception;
        return milestone;
    }

    private String                  description;
    private TimePeriod<Millisecond> duration;
    private long                    endTimestamp;
    private Throwable               error;
    private String                  name;
    private long                    startTimestamp;

    private Milestone() { }

    public @Nonnull String getDescription() {
        return description;
    }

    public @Nonnull TimePeriod<Millisecond> getDuration() {
        return duration;
    }

    public @Nonnegative long getEndTimestamp() {
        return endTimestamp;
    }

    public @Nullable Throwable getError() {
        return error;
    }

    public @Nonnull String getName() {
        return name;
    }

    public @Nonnegative long getStartTimestamp() {
        return startTimestamp;
    }
}
