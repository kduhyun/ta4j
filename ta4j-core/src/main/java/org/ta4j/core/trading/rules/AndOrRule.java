/*******************************************************************************
 *   The MIT License (MIT)
 *
 *   Copyright (c) 2014-2017 Marc de Verdelhan, 2017-2018 Ta4j Organization
 *   & respective authors (see AUTHORS)
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy of
 *   this software and associated documentation files (the "Software"), to deal in
 *   the Software without restriction, including without limitation the rights to
 *   use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 *   the Software, and to permit persons to whom the Software is furnished to do so,
 *   subject to the following conditions:
 *
 *   The above copyright notice and this permission notice shall be included in all
 *   copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 *   FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 *   COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 *   IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 *   CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package org.ta4j.core.trading.rules;

import org.ta4j.core.Rule;
import org.ta4j.core.TradingRecord;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class AndOrRule extends AbstractRule {

    private Rule[] rules;
    private int satisfiedCount = 0;


    protected void init(int satisfiedCount, Rule ... rules) {
        this.rules = rules;
        this.satisfiedCount = satisfiedCount;
    }

    @Override
    public boolean isSatisfied(int index, TradingRecord tradingRecord) {
        Boolean[] ruleSatisfied = new Boolean[rules.length];
        int satisfiedCount = this.satisfiedCount;

        boolean satisfied = false;
        for (int i = 0; i < rules.length; i++) {
            ruleSatisfied[i] = new Boolean(rules[i].isSatisfied(index, tradingRecord));
            if(ruleSatisfied[i]) {
                satisfiedCount--;
            }
        }

        if(satisfiedCount <= 0) {

            satisfiedRuleList = IntStream.range(0, rules.length)
                    .boxed()
                    .filter(integer -> ruleSatisfied[integer])
                    .map(i -> getSatisfiedRuleDescription(rules[i]))
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());

            satisfied = true;
        }

        traceIsSatisfied(index, satisfied);
        return satisfied;
    }

    public List<String> getSatisfiedRuleDescription(Rule rule) {
        if(!(rule instanceof AndRule) && !(rule instanceof OrRule)) {

            return Collections.singletonList(String.format("[%s]", rule.getDescription()));
        } else {
            return rule.getSatisfiedRuleList();
        }
    }
}
