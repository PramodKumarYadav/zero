package org.powertester.annotations;

import static org.junit.jupiter.params.ParameterizedTest.INDEX_PLACEHOLDER;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.params.ParameterizedTest;

@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ParameterizedTest(name = "[" + INDEX_PLACEHOLDER + "] " + "{1}-{2}")
public @interface CSVTest {}
