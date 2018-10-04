package com.amitinside.dependency.graph.osgi;

import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;

import aQute.bnd.util.dto.DTO;

public class ResourceInfo extends DTO implements Comparable<ResourceInfo> {

    public Requirement requirement;
    public Set<Resource> requiredResources;

    @Override
    public int compareTo(final ResourceInfo o) {
        final int result = ComparisonChain.start()
                .compare(requirement.getNamespace(), o.requirement.getNamespace(), Ordering.natural()).result();
        return (int) Math.signum(result);
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
        return new ToStringBuilder(this).append("requirement", requirement.getNamespace()).toString();
    }

}
