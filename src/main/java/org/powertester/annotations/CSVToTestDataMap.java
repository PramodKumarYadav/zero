package org.powertester.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.powertester.data.CSVAggregator;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@AggregateWith(CSVAggregator.class)
public @interface CSVToTestDataMap {}
