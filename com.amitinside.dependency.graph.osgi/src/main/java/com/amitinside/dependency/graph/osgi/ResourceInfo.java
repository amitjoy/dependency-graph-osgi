/*******************************************************************************
 * Copyright (c) 2018 Amit Kumar Mondal
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package com.amitinside.dependency.graph.osgi;

import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;

import com.google.common.collect.ComparisonChain;

public final class ResourceInfo implements Comparable<ResourceInfo> {

    public Requirement requirement;
    public Set<Resource> requiredResources;

    @Override
    public int compareTo(final ResourceInfo o) {
        return ComparisonChain.start().compare(requirement.getNamespace(), o.requirement.getNamespace()).result();
    }

    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof ResourceInfo)) {
            return false;
        }
        final ResourceInfo castOther = (ResourceInfo) other;
        return new EqualsBuilder().append(requirement, castOther.requirement)
                .append(requiredResources, castOther.requiredResources).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(requirement).append(requiredResources).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("requirement", requirement)
                .append("requiredResources", requiredResources).toString();
    }

}
