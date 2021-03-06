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

package grakn.client.concept.type.impl;

import grakn.client.Grakn;
import grakn.client.common.exception.GraknClientException;
import grakn.client.concept.type.AttributeType;
import grakn.client.concept.type.EntityType;
import grakn.client.concept.type.RelationType;
import grakn.client.concept.type.RoleType;
import grakn.client.concept.type.ThingType;
import grakn.client.concept.type.Type;
import grakn.protocol.ConceptProto;
import grakn.protocol.ConceptProto.Type.SetSupertype;
import grakn.protocol.ConceptProto.TypeMethod;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

import static grakn.client.common.exception.ErrorMessage.Concept.INVALID_CONCEPT_CASTING;
import static grakn.client.common.exception.ErrorMessage.Concept.MISSING_IID;
import static grakn.client.common.exception.ErrorMessage.Concept.MISSING_TRANSACTION;
import static grakn.client.common.exception.ErrorMessage.Concept.BAD_ENCODING;
import static grakn.client.concept.proto.ConceptProtoBuilder.type;
import static grakn.common.util.Objects.className;

public abstract class TypeImpl implements Type {

    private final String label;
    private final boolean isRoot;
    private final int hash;

    TypeImpl(final String label, final boolean isRoot) {
        if (label == null || label.isEmpty()) throw new GraknClientException(MISSING_IID);
        this.label = label;
        this.isRoot = isRoot;
        this.hash = Objects.hash(this.label);
    }

    public static TypeImpl of(final ConceptProto.Type typeProto) {
        switch (typeProto.getEncoding()) {
            case ROLE_TYPE:
                return RoleTypeImpl.of(typeProto);
            case RULE:
                return RuleImpl.of(typeProto);
            case UNRECOGNIZED:
                throw new GraknClientException(BAD_ENCODING.message(typeProto.getEncoding()));
            default:
                return ThingTypeImpl.of(typeProto);

        }
    }

    @Override
    public final String getLabel() {
        return label;
    }

    @Override
    public final boolean isRoot() {
        return isRoot;
    }

    @Override
    public TypeImpl asType() {
        return this;
    }

    @Override
    public ThingTypeImpl asThingType() {
        throw new GraknClientException(INVALID_CONCEPT_CASTING.message(className(this.getClass()), className(ThingType.class)));
    }

    @Override
    public EntityTypeImpl asEntityType() {
        throw new GraknClientException(INVALID_CONCEPT_CASTING.message(className(this.getClass()), className(EntityType.class)));
    }

    @Override
    public AttributeTypeImpl asAttributeType() {
        throw new GraknClientException(INVALID_CONCEPT_CASTING.message(className(this.getClass()), className(AttributeType.class)));
    }

    @Override
    public RelationTypeImpl asRelationType() {
        throw new GraknClientException(INVALID_CONCEPT_CASTING.message(className(this.getClass()), className(RelationType.class)));
    }

    @Override
    public RoleTypeImpl asRoleType() {
        throw new GraknClientException(INVALID_CONCEPT_CASTING.message(className(this.getClass()), className(RoleType.class)));
    }

    @Override
    public String toString() {
        return className(this.getClass()) + "[label: " + label + "]";
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final TypeImpl that = (TypeImpl) o;
        return this.label.equals(that.label);
    }

    @Override
    public int hashCode() {
        return hash;
    }

    public abstract static class Remote implements Type.Remote {

        private final Grakn.Transaction transaction;
        private final String label;
        private final boolean isRoot;
        private final int hash;

        Remote(final Grakn.Transaction transaction, final String label, final boolean isRoot) {
            if (transaction == null) throw new GraknClientException(MISSING_TRANSACTION);
            this.transaction = transaction;
            this.label = label;
            this.isRoot = isRoot;
            this.hash = Objects.hash(transaction, label);
        }

        public static TypeImpl.Remote of(final Grakn.Transaction transaction, final ConceptProto.Type type) {
            switch (type.getEncoding()) {
                case ENTITY_TYPE:
                    return EntityTypeImpl.Remote.of(transaction, type);
                case RELATION_TYPE:
                    return RelationTypeImpl.Remote.of(transaction, type);
                case ATTRIBUTE_TYPE:
                    return AttributeTypeImpl.Remote.of(transaction, type);
                case ROLE_TYPE:
                    return RoleTypeImpl.Remote.of(transaction, type);
                case THING_TYPE:
                    return ThingTypeImpl.Remote.of(transaction, type);
                case RULE:
                    return RuleImpl.Remote.of(transaction, type);
                case UNRECOGNIZED:
                default:
                    throw new GraknClientException(BAD_ENCODING.message(type.getEncoding()));
            }
        }

        @Override
        public final String getLabel() {
            return label;
        }

        @Override
        public boolean isRoot() {
            return isRoot;
        }

        @Override
        public final void setLabel(final String label) {
            final TypeMethod.Req method = TypeMethod.Req.newBuilder()
                    .setTypeSetLabelReq(ConceptProto.Type.SetLabel.Req.newBuilder()
                            .setLabel(label)).build();
            execute(method);
        }

        @Override
        public final boolean isAbstract() {
            final TypeMethod.Req method = TypeMethod.Req.newBuilder()
                    .setTypeIsAbstractReq(ConceptProto.Type.IsAbstract.Req.getDefaultInstance()).build();

            return execute(method).getTypeIsAbstractRes().getAbstract();
        }

        @Override
        public TypeImpl.Remote asType() {
            return this;
        }

        @Override
        public ThingTypeImpl.Remote asThingType() {
            throw new GraknClientException(INVALID_CONCEPT_CASTING.message(className(this.getClass()), className(ThingType.class)));
        }

        @Override
        public EntityTypeImpl.Remote asEntityType() {
            throw new GraknClientException(INVALID_CONCEPT_CASTING.message(className(this.getClass()), className(EntityType.class)));
        }

        @Override
        public RelationTypeImpl.Remote asRelationType() {
            throw new GraknClientException(INVALID_CONCEPT_CASTING.message(className(this.getClass()), className(RelationType.class)));
        }

        @Override
        public AttributeTypeImpl.Remote asAttributeType() {
            throw new GraknClientException(INVALID_CONCEPT_CASTING.message(className(this.getClass()), className(AttributeType.class)));
        }

        @Override
        public RoleTypeImpl.Remote asRoleType() {
            throw new GraknClientException(INVALID_CONCEPT_CASTING.message(className(this.getClass()), className(RoleType.class)));
        }

        void setSupertypeExecute(final Type type) {
            execute(TypeMethod.Req.newBuilder().setTypeSetSupertypeReq(SetSupertype.Req.newBuilder().setType(type(type))).build());
        }

        @Nullable
        <TYPE extends TypeImpl> TYPE getSupertypeExecute(final Function<TypeImpl, TYPE> typeConstructor) {
            final TypeMethod.Req method = TypeMethod.Req.newBuilder()
                    .setTypeGetSupertypeReq(ConceptProto.Type.GetSupertype.Req.getDefaultInstance()).build();

            final ConceptProto.Type.GetSupertype.Res response = execute(method).getTypeGetSupertypeRes();

            switch (response.getResCase()) {
                case TYPE:
                    return typeConstructor.apply(TypeImpl.of(response.getType()));
                case RES_NOT_SET:
                default:
                    return null;
            }
        }

        <TYPE extends TypeImpl> Stream<TYPE> getSupertypes(final Function<TypeImpl, TYPE> typeConstructor) {
            final TypeMethod.Iter.Req method = TypeMethod.Iter.Req.newBuilder()
                    .setTypeGetSupertypesIterReq(ConceptProto.Type.GetSupertypes.Iter.Req.getDefaultInstance()).build();
            return stream(method, res -> res.getTypeGetSupertypesIterRes().getType()).map(typeConstructor);
        }

        <TYPE extends TypeImpl> Stream<TYPE> getSubtypes(final Function<TypeImpl, TYPE> typeConstructor) {
            final TypeMethod.Iter.Req method = TypeMethod.Iter.Req.newBuilder()
                    .setTypeGetSubtypesIterReq(ConceptProto.Type.GetSubtypes.Iter.Req.getDefaultInstance()).build();
            return stream(method, res -> res.getTypeGetSubtypesIterRes().getType()).map(typeConstructor);
        }

        @Override
        public final void delete() {
            final TypeMethod.Req method = TypeMethod.Req.newBuilder()
                    .setTypeDeleteReq(ConceptProto.Type.Delete.Req.getDefaultInstance()).build();
            execute(method);
        }

        @Override
        public final boolean isDeleted() {
            return transaction.concepts().getType(getLabel()) == null;
        }

        final Grakn.Transaction tx() {
            return transaction;
        }

        Stream<TypeImpl> stream(final TypeMethod.Iter.Req request, final Function<TypeMethod.Iter.Res, ConceptProto.Type> typeGetter) {
            return transaction.concepts().iterateTypeMethod(
                    getLabel(), null, request, response -> TypeImpl.of(typeGetter.apply(response))
            );
        }

        TypeMethod.Res execute(final TypeMethod.Req typeMethod) {
            return transaction.concepts().runTypeMethod(getLabel(), null, typeMethod)
                    .getConceptMethodTypeRes().getResponse();
        }

        @Override
        public String toString() {
            return className(this.getClass()) + "[label: " + label + "]";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final TypeImpl.Remote that = (TypeImpl.Remote) o;
            return (this.transaction.equals(that.transaction) &&
                    this.getLabel().equals(that.getLabel()));
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }
}
