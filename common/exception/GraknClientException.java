/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package grakn.client.common.exception;

import io.grpc.StatusRuntimeException;

import javax.annotation.Nullable;

public class GraknClientException extends RuntimeException {

    private String statusCode;

    public GraknClientException(final String error) {
        super(error);
    }

    public GraknClientException(final ErrorMessage error) {
        super(error.toString());
        assert !getMessage().contains("%s");
    }

    public GraknClientException(final StatusRuntimeException statusRuntimeException) {
        super(statusRuntimeException.getMessage(), statusRuntimeException);
        this.statusCode = statusRuntimeException.getStatus().getCode().name();
    }

    public GraknClientException(final Exception e) {
        super(e);
    }

    public String getName() {
        return this.getClass().getName();
    }

    @Nullable
    public String getStatusCode() { return this.statusCode; }
}
