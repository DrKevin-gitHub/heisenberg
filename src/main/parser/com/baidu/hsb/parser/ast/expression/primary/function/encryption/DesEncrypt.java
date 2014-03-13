/**
 * Baidu.com,Inc.
 * Copyright (c) 2000-2013 All Rights Reserved.
 */
package com.baidu.hsb.parser.ast.expression.primary.function.encryption;

import java.util.List;

import com.baidu.hsb.parser.ast.expression.Expression;
import com.baidu.hsb.parser.ast.expression.primary.function.FunctionExpression;

/**
 * @author xiongzhao@baidu.com
 */
public class DesEncrypt extends FunctionExpression {
    public DesEncrypt(List<Expression> arguments) {
        super("DES_ENCRYPT", arguments);
    }

    @Override
    public FunctionExpression constructFunction(List<Expression> arguments) {
        return new DesEncrypt(arguments);
    }

}
