/**
 * Baidu.com,Inc.
 * Copyright (c) 2000-2013 All Rights Reserved.
 */
package com.baidu.hsb.parser.ast.expression.primary.function.info;

import java.util.List;

import com.baidu.hsb.parser.ast.expression.Expression;
import com.baidu.hsb.parser.ast.expression.primary.function.FunctionExpression;

/**
 * @author xiongzhao@baidu.com
 */
public class User extends FunctionExpression {
    public User(List<Expression> arguments) {
        super("USER", arguments);
    }

    @Override
    public FunctionExpression constructFunction(List<Expression> arguments) {
        return new User(arguments);
    }

}
