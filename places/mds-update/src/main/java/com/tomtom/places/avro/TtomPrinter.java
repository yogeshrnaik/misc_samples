package com.tomtom.places.avro;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.teleatlas.global.common.ddct.DictionaryRangeItem;
import com.teleatlas.global.common.ddct.Identifiable;
import com.tomtom.cpu.api.features.Association;
import com.tomtom.cpu.api.features.Attribute;
import com.tomtom.cpu.api.features.AttributedObject;
import com.tomtom.cpu.api.features.Feature;
import com.tomtom.cpu.api.features.NonSpatialObject;
import com.tomtom.cpu.api.values.DictionaryRangeItemSet;

public class TtomPrinter {

    private static final String INDENTATION = "    ";

    private final PrintWriter out;
    private boolean withAssociations;
    private boolean withFeatureId;
    private final Set<String> ignoredAttributes;

    private TtomPrinter(PrintWriter out) {
        this.out = out;
        withAssociations = true;
        withFeatureId = true;
        ignoredAttributes = Sets.newHashSet();
    }

    public static TtomPrinter to(PrintWriter out) {
        return new TtomPrinter(out);
    }

    public static TtomPrinter toSystemOut() {
        return new TtomPrinter(systemOut());
    }

    public TtomPrinter withAssociations(boolean withAssociations) {
        this.withAssociations = withAssociations;
        return this;
    }

    public TtomPrinter withFeatureId(boolean withFeatureId) {
        this.withFeatureId = withFeatureId;
        return this;
    }

    public TtomPrinter withIgnoredAttribute(String attributeTypeShortName) {
        this.ignoredAttributes.add(attributeTypeShortName);
        return this;
    }

    public void printFeature(Feature<?> f) {
        if (withFeatureId) {
            out.println(f.getId());
        }
        out.println(INDENTATION + "Type: " + type(f.getType()));
        out.println(INDENTATION + "Geometry: " + f.getGeometry().toString());
        out.println(INDENTATION + "Attributes:");
        for (Attribute<?> attr : sortedAttributes(f)) {
            printAttribute(attr, 1);
        }
        if (withAssociations) {
            out.println(INDENTATION + "Associations:");
            for (Association ass : f.getAssociations()) {
                printAssociation(ass);
            }
        }
    }

    public void printAssociation(Association ass) {
        out.println(INDENTATION + ass.getId() + " Type " + type(ass.getType()) + " Sequence " + ass.getSequenceNumber());
        out.println(INDENTATION + INDENTATION + "Source feature: " + ass.getSourceFeature().getId());
        out.println(INDENTATION + INDENTATION + "Target feature: " + ass.getTargetFeature().getId());
        out.println(INDENTATION + INDENTATION + "Attributes:");
        for (Attribute<?> attr : sortedAttributes(ass)) {
            printAttribute(attr, 3);
        }
    }

    public void printAttribute(Attribute<?> attribute, int level) {
        if (ignoredAttributes.contains(attribute.getType().getTypeShortName())) {
            return;
        }

        String indent = Strings.repeat(INDENTATION, level);
        out.println(indent + type(attribute.getType()));
        if (attribute.getType().isComposite()) {
            NonSpatialObject attributeValue = (NonSpatialObject)attribute.getValue();
            for (Attribute<?> subAttribute : sortedAttributes(attributeValue)) {
                printAttribute(subAttribute, level + 1);
            }
        } else {
            out.println(indent + INDENTATION + attributeValue(attribute));
        }
    }

    private static String attributeValue(Attribute<?> attribute) {
        Object value = attribute.getValue();
        if (value instanceof String) {
            return (String)value;
        } else if (value instanceof DictionaryRangeItem) {
            return ((DictionaryRangeItem)value).getValue();
        } else if (value instanceof DictionaryRangeItemSet) {
            return ((DictionaryRangeItemSet)value).getValue();
        } else {
            return String.valueOf(value);
        }
    }

    private static String type(Identifiable type) {
        return type.getNameSpace() + ":" + type.getTypeName();
    }

    private static List<Attribute<?>> sortedAttributes(AttributedObject attributedObject) {
        List<Attribute<?>> attributes = Lists.newArrayList(attributedObject.getAttributes());
        Collections.sort(attributes, new Comparator<Attribute<?>>() {

            @Override
            public int compare(Attribute<?> o1, Attribute<?> o2) {
                return o1.getType().getTypeName().compareTo(o2.getType().getTypeName());
            }
        });

        return attributes;
    }

    private static PrintWriter systemOut() {
        return new PrintWriter(System.out, true);
    }
}
