/**
 * Baidu.com,Inc.
 * Copyright (c) 2000-2013 All Rights Reserved.
 */
package com.baidu.hsb.parser.ast.expression.primary.function.misc;

import java.util.List;

import com.baidu.hsb.parser.ast.expression.Expression;
import com.baidu.hsb.parser.ast.expression.primary.function.FunctionExpression;

/**
 * @author xiongzhao@baidu.com
 */
public class ReleaseLock extends FunctionExpression {
    public ReleaseLock(List<Expression> arguments) {
        super("RELEASE_LOCK", arguments);
    }

    @Override
    public FunctionExpression constructFunction(List<Expression> arguments) {
        return new ReleaseLock(arguments);
    }

}
