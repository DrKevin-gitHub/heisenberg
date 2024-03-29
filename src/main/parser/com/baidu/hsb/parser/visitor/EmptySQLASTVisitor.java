/**
 * Baidu.com,Inc.
 * Copyright (c) 2000-2013 All Rights Reserved.
 */
package com.baidu.hsb.parser.visitor;

import java.util.Collection;

import com.baidu.hsb.parser.ast.ASTNode;
import com.baidu.hsb.parser.ast.expression.BinaryOperatorExpression;
import com.baidu.hsb.parser.ast.expression.PolyadicOperatorExpression;
import com.baidu.hsb.parser.ast.expression.UnaryOperatorExpression;
import com.baidu.hsb.parser.ast.expression.comparison.BetweenAndExpression;
import com.baidu.hsb.parser.ast.expression.comparison.ComparisionEqualsExpression;
import com.baidu.hsb.parser.ast.expression.comparison.ComparisionIsExpression;
import com.baidu.hsb.parser.ast.expression.comparison.ComparisionNullSafeEqualsExpression;
import com.baidu.hsb.parser.ast.expression.comparison.InExpression;
import com.baidu.hsb.parser.ast.expression.logical.LogicalAndExpression;
import com.baidu.hsb.parser.ast.expression.logical.LogicalOrExpression;
import com.baidu.hsb.parser.ast.expression.misc.InExpressionList;
import com.baidu.hsb.parser.ast.expression.misc.UserExpression;
import com.baidu.hsb.parser.ast.expression.primary.CaseWhenOperatorExpression;
import com.baidu.hsb.parser.ast.expression.primary.DefaultValue;
import com.baidu.hsb.parser.ast.expression.primary.ExistsPrimary;
import com.baidu.hsb.parser.ast.expression.primary.Identifier;
import com.baidu.hsb.parser.ast.expression.primary.MatchExpression;
import com.baidu.hsb.parser.ast.expression.primary.ParamMarker;
import com.baidu.hsb.parser.ast.expression.primary.PlaceHolder;
import com.baidu.hsb.parser.ast.expression.primary.RowExpression;
import com.baidu.hsb.parser.ast.expression.primary.SysVarPrimary;
import com.baidu.hsb.parser.ast.expression.primary.UsrDefVarPrimary;
import com.baidu.hsb.parser.ast.expression.primary.function.FunctionExpression;
import com.baidu.hsb.parser.ast.expression.primary.function.cast.Cast;
import com.baidu.hsb.parser.ast.expression.primary.function.cast.Convert;
import com.baidu.hsb.parser.ast.expression.primary.function.datetime.Extract;
import com.baidu.hsb.parser.ast.expression.primary.function.datetime.GetFormat;
import com.baidu.hsb.parser.ast.expression.primary.function.datetime.Timestampadd;
import com.baidu.hsb.parser.ast.expression.primary.function.datetime.Timestampdiff;
import com.baidu.hsb.parser.ast.expression.primary.function.groupby.Avg;
import com.baidu.hsb.parser.ast.expression.primary.function.groupby.Count;
import com.baidu.hsb.parser.ast.expression.primary.function.groupby.GroupConcat;
import com.baidu.hsb.parser.ast.expression.primary.function.groupby.Max;
import com.baidu.hsb.parser.ast.expression.primary.function.groupby.Min;
import com.baidu.hsb.parser.ast.expression.primary.function.groupby.Sum;
import com.baidu.hsb.parser.ast.expression.primary.function.string.Char;
import com.baidu.hsb.parser.ast.expression.primary.function.string.Trim;
import com.baidu.hsb.parser.ast.expression.primary.literal.IntervalPrimary;
import com.baidu.hsb.parser.ast.expression.primary.literal.LiteralBitField;
import com.baidu.hsb.parser.ast.expression.primary.literal.LiteralBoolean;
import com.baidu.hsb.parser.ast.expression.primary.literal.LiteralHexadecimal;
import com.baidu.hsb.parser.ast.expression.primary.literal.LiteralNull;
import com.baidu.hsb.parser.ast.expression.primary.literal.LiteralNumber;
import com.baidu.hsb.parser.ast.expression.primary.literal.LiteralString;
import com.baidu.hsb.parser.ast.expression.string.LikeExpression;
import com.baidu.hsb.parser.ast.expression.type.CollateExpression;
import com.baidu.hsb.parser.ast.fragment.GroupBy;
import com.baidu.hsb.parser.ast.fragment.Limit;
import com.baidu.hsb.parser.ast.fragment.OrderBy;
import com.baidu.hsb.parser.ast.fragment.ddl.ColumnDefinition;
import com.baidu.hsb.parser.ast.fragment.ddl.TableOptions;
import com.baidu.hsb.parser.ast.fragment.ddl.datatype.DataType;
import com.baidu.hsb.parser.ast.fragment.ddl.index.IndexColumnName;
import com.baidu.hsb.parser.ast.fragment.ddl.index.IndexOption;
import com.baidu.hsb.parser.ast.fragment.tableref.Dual;
import com.baidu.hsb.parser.ast.fragment.tableref.IndexHint;
import com.baidu.hsb.parser.ast.fragment.tableref.InnerJoin;
import com.baidu.hsb.parser.ast.fragment.tableref.NaturalJoin;
import com.baidu.hsb.parser.ast.fragment.tableref.OuterJoin;
import com.baidu.hsb.parser.ast.fragment.tableref.StraightJoin;
import com.baidu.hsb.parser.ast.fragment.tableref.SubqueryFactor;
import com.baidu.hsb.parser.ast.fragment.tableref.TableRefFactor;
import com.baidu.hsb.parser.ast.fragment.tableref.TableReferences;
import com.baidu.hsb.parser.ast.stmt.dal.DALSetCharacterSetStatement;
import com.baidu.hsb.parser.ast.stmt.dal.DALSetNamesStatement;
import com.baidu.hsb.parser.ast.stmt.dal.DALSetStatement;
import com.baidu.hsb.parser.ast.stmt.dal.ShowAuthors;
import com.baidu.hsb.parser.ast.stmt.dal.ShowBinLogEvent;
import com.baidu.hsb.parser.ast.stmt.dal.ShowBinaryLog;
import com.baidu.hsb.parser.ast.stmt.dal.ShowCharaterSet;
import com.baidu.hsb.parser.ast.stmt.dal.ShowCollation;
import com.baidu.hsb.parser.ast.stmt.dal.ShowColumns;
import com.baidu.hsb.parser.ast.stmt.dal.ShowContributors;
import com.baidu.hsb.parser.ast.stmt.dal.ShowCreate;
import com.baidu.hsb.parser.ast.stmt.dal.ShowDatabases;
import com.baidu.hsb.parser.ast.stmt.dal.ShowEngine;
import com.baidu.hsb.parser.ast.stmt.dal.ShowEngines;
import com.baidu.hsb.parser.ast.stmt.dal.ShowErrors;
import com.baidu.hsb.parser.ast.stmt.dal.ShowEvents;
import com.baidu.hsb.parser.ast.stmt.dal.ShowFunctionCode;
import com.baidu.hsb.parser.ast.stmt.dal.ShowFunctionStatus;
import com.baidu.hsb.parser.ast.stmt.dal.ShowGrants;
import com.baidu.hsb.parser.ast.stmt.dal.ShowIndex;
import com.baidu.hsb.parser.ast.stmt.dal.ShowMasterStatus;
import com.baidu.hsb.parser.ast.stmt.dal.ShowOpenTables;
import com.baidu.hsb.parser.ast.stmt.dal.ShowPlugins;
import com.baidu.hsb.parser.ast.stmt.dal.ShowPrivileges;
import com.baidu.hsb.parser.ast.stmt.dal.ShowProcedureCode;
import com.baidu.hsb.parser.ast.stmt.dal.ShowProcedureStatus;
import com.baidu.hsb.parser.ast.stmt.dal.ShowProcesslist;
import com.baidu.hsb.parser.ast.stmt.dal.ShowProfile;
import com.baidu.hsb.parser.ast.stmt.dal.ShowProfiles;
import com.baidu.hsb.parser.ast.stmt.dal.ShowSlaveHosts;
import com.baidu.hsb.parser.ast.stmt.dal.ShowSlaveStatus;
import com.baidu.hsb.parser.ast.stmt.dal.ShowStatus;
import com.baidu.hsb.parser.ast.stmt.dal.ShowTableStatus;
import com.baidu.hsb.parser.ast.stmt.dal.ShowTables;
import com.baidu.hsb.parser.ast.stmt.dal.ShowTriggers;
import com.baidu.hsb.parser.ast.stmt.dal.ShowVariables;
import com.baidu.hsb.parser.ast.stmt.dal.ShowWarnings;
import com.baidu.hsb.parser.ast.stmt.ddl.DDLAlterTableStatement;
import com.baidu.hsb.parser.ast.stmt.ddl.DDLCreateIndexStatement;
import com.baidu.hsb.parser.ast.stmt.ddl.DDLCreateTableStatement;
import com.baidu.hsb.parser.ast.stmt.ddl.DDLDropIndexStatement;
import com.baidu.hsb.parser.ast.stmt.ddl.DDLDropTableStatement;
import com.baidu.hsb.parser.ast.stmt.ddl.DDLRenameTableStatement;
import com.baidu.hsb.parser.ast.stmt.ddl.DDLTruncateStatement;
import com.baidu.hsb.parser.ast.stmt.ddl.DescTableStatement;
import com.baidu.hsb.parser.ast.stmt.ddl.DDLAlterTableStatement.AlterSpecification;
import com.baidu.hsb.parser.ast.stmt.dml.DMLCallStatement;
import com.baidu.hsb.parser.ast.stmt.dml.DMLDeleteStatement;
import com.baidu.hsb.parser.ast.stmt.dml.DMLInsertStatement;
import com.baidu.hsb.parser.ast.stmt.dml.DMLReplaceStatement;
import com.baidu.hsb.parser.ast.stmt.dml.DMLSelectStatement;
import com.baidu.hsb.parser.ast.stmt.dml.DMLSelectUnionStatement;
import com.baidu.hsb.parser.ast.stmt.dml.DMLUpdateStatement;
import com.baidu.hsb.parser.ast.stmt.extension.ExtDDLCreatePolicy;
import com.baidu.hsb.parser.ast.stmt.extension.ExtDDLDropPolicy;
import com.baidu.hsb.parser.ast.stmt.mts.MTSReleaseStatement;
import com.baidu.hsb.parser.ast.stmt.mts.MTSRollbackStatement;
import com.baidu.hsb.parser.ast.stmt.mts.MTSSavepointStatement;
import com.baidu.hsb.parser.ast.stmt.mts.MTSSetTransactionStatement;
import com.baidu.hsb.parser.util.Pair;

/**
 * @author xiongzhao@baidu.com
 */
public class EmptySQLASTVisitor implements SQLASTVisitor {

    @SuppressWarnings({ "rawtypes" })
    private void visitInternal(Object obj) {
        if (obj == null) return;
        if (obj instanceof ASTNode) {
            ((ASTNode) obj).accept(this);
        } else if (obj instanceof Collection) {
            for (Object o : (Collection) obj) {
                visitInternal(o);
            }
        } else if (obj instanceof Pair) {
            visitInternal(((Pair) obj).getKey());
            visitInternal(((Pair) obj).getValue());
        }
    }

    @Override
    public void visit(BetweenAndExpression node) {
        visitInternal(node.getFirst());
        visitInternal(node.getSecond());
        visitInternal(node.getThird());
    }

    @Override
    public void visit(ComparisionIsExpression node) {
        visitInternal(node.getOperand());
    }

    @Override
    public void visit(InExpressionList node) {
        visitInternal(node.getList());
    }

    @Override
    public void visit(LikeExpression node) {
        visitInternal(node.getFirst());
        visitInternal(node.getSecond());
        visitInternal(node.getThird());
    }

    @Override
    public void visit(CollateExpression node) {
        visitInternal(node.getString());
    }

    @Override
    public void visit(UserExpression node) {
    }

    @Override
    public void visit(UnaryOperatorExpression node) {
        visitInternal(node.getOperand());
    }

    @Override
    public void visit(BinaryOperatorExpression node) {
        visitInternal(node.getLeftOprand());
        visitInternal(node.getRightOprand());
    }

    @Override
    public void visit(PolyadicOperatorExpression node) {
        for (int i = 0, len = node.getArity(); i < len; ++i) {
            visitInternal(node.getOperand(i));
        }
    }

    @Override
    public void visit(LogicalAndExpression node) {
        visit((PolyadicOperatorExpression) node);
    }

    @Override
    public void visit(LogicalOrExpression node) {
        visit((PolyadicOperatorExpression) node);
    }

    @Override
    public void visit(ComparisionEqualsExpression node) {
        visit((BinaryOperatorExpression) node);
    }

    @Override
    public void visit(ComparisionNullSafeEqualsExpression node) {
        visit((BinaryOperatorExpression) node);
    }

    @Override
    public void visit(InExpression node) {
        visit((BinaryOperatorExpression) node);
    }

    @Override
    public void visit(FunctionExpression node) {
        visitInternal(node.getArguments());
    }

    @Override
    public void visit(Char node) {
        visit((FunctionExpression) node);
    }

    @Override
    public void visit(Convert node) {
        visit((FunctionExpression) node);
    }

    @Override
    public void visit(Trim node) {
        visit((FunctionExpression) node);
        visitInternal(node.getRemainString());
        visitInternal(node.getString());
    }

    @Override
    public void visit(Cast node) {
        visit((FunctionExpression) node);
        visitInternal(node.getExpr());
        visitInternal(node.getTypeInfo1());
        visitInternal(node.getTypeInfo2());

    }

    @Override
    public void visit(Avg node) {
        visit((FunctionExpression) node);
    }

    @Override
    public void visit(Max node) {
        visit((FunctionExpression) node);
    }

    @Override
    public void visit(Min node) {
        visit((FunctionExpression) node);
    }

    @Override
    public void visit(Sum node) {
        visit((FunctionExpression) node);
    }

    @Override
    public void visit(Count node) {
        visit((FunctionExpression) node);
    }

    @Override
    public void visit(GroupConcat node) {
        visit((FunctionExpression) node);
        visitInternal(node.getAppendedColumnNames());
        visitInternal(node.getOrderBy());
    }

    @Override
    public void visit(Timestampdiff node) {
    }

    @Override
    public void visit(Timestampadd node) {
    }

    @Override
    public void visit(Extract node) {
    }

    @Override
    public void visit(GetFormat node) {
    }

    @Override
    public void visit(IntervalPrimary node) {
        visitInternal(node.getQuantity());
    }

    @Override
    public void visit(LiteralBitField node) {
    }

    @Override
    public void visit(LiteralBoolean node) {
    }

    @Override
    public void visit(LiteralHexadecimal node) {
    }

    @Override
    public void visit(LiteralNull node) {
    }

    @Override
    public void visit(LiteralNumber node) {
    }

    @Override
    public void visit(LiteralString node) {
    }

    @Override
    public void visit(CaseWhenOperatorExpression node) {
        visitInternal(node.getComparee());
        visitInternal(node.getElseResult());
        visitInternal(node.getWhenList());
    }

    @Override
    public void visit(DefaultValue node) {
    }

    @Override
    public void visit(ExistsPrimary node) {
        visitInternal(node.getSubquery());
    }

    @Override
    public void visit(PlaceHolder node) {
    }

    @Override
    public void visit(Identifier node) {
    }

    @Override
    public void visit(MatchExpression node) {
        visitInternal(node.getColumns());
        visitInternal(node.getPattern());
    }

    @Override
    public void visit(ParamMarker node) {
    }

    @Override
    public void visit(RowExpression node) {
        visitInternal(node.getRowExprList());
    }

    @Override
    public void visit(SysVarPrimary node) {
    }

    @Override
    public void visit(UsrDefVarPrimary node) {
    }

    @Override
    public void visit(IndexHint node) {
    }

    @Override
    public void visit(InnerJoin node) {
        visitInternal(node.getLeftTableRef());
        visitInternal(node.getOnCond());
        visitInternal(node.getRightTableRef());
    }

    @Override
    public void visit(NaturalJoin node) {
        visitInternal(node.getLeftTableRef());
        visitInternal(node.getRightTableRef());
    }

    @Override
    public void visit(OuterJoin node) {
        visitInternal(node.getLeftTableRef());
        visitInternal(node.getOnCond());
        visitInternal(node.getRightTableRef());
    }

    @Override
    public void visit(StraightJoin node) {
        visitInternal(node.getLeftTableRef());
        visitInternal(node.getOnCond());
        visitInternal(node.getRightTableRef());
    }

    @Override
    public void visit(SubqueryFactor node) {
        visitInternal(node.getSubquery());
    }

    @Override
    public void visit(TableReferences node) {
        visitInternal(node.getTableReferenceList());
    }

    @Override
    public void visit(TableRefFactor node) {
        visitInternal(node.getHintList());
        visitInternal(node.getTable());
    }

    @Override
    public void visit(Dual dual) {
    }

    @Override
    public void visit(GroupBy node) {
        visitInternal(node.getOrderByList());
    }

    @Override
    public void visit(Limit node) {
        visitInternal(node.getOffset());
        visitInternal(node.getSize());
    }

    @Override
    public void visit(OrderBy node) {
        visitInternal(node.getOrderByList());
    }

    @Override
    public void visit(ColumnDefinition columnDefinition) {
    }

    @Override
    public void visit(IndexOption indexOption) {
    }

    @Override
    public void visit(IndexColumnName indexColumnName) {
    }

    @Override
    public void visit(TableOptions node) {
    }

    @Override
    public void visit(AlterSpecification node) {
    }

    @Override
    public void visit(DataType node) {
    }

    @Override
    public void visit(ShowAuthors node) {
    }

    @Override
    public void visit(ShowBinaryLog node) {
    }

    @Override
    public void visit(ShowBinLogEvent node) {
        visitInternal(node.getLimit());
        visitInternal(node.getPos());
    }

    @Override
    public void visit(ShowCharaterSet node) {
        visitInternal(node.getWhere());
    }

    @Override
    public void visit(ShowCollation node) {
        visitInternal(node.getWhere());
    }

    @Override
    public void visit(ShowColumns node) {
        visitInternal(node.getTable());
        visitInternal(node.getWhere());
    }

    @Override
    public void visit(ShowContributors node) {
    }

    @Override
    public void visit(ShowCreate node) {
        visitInternal(node.getId());
    }

    @Override
    public void visit(ShowDatabases node) {
        visitInternal(node.getWhere());
    }

    @Override
    public void visit(ShowEngine node) {
    }

    @Override
    public void visit(ShowEngines node) {
    }

    @Override
    public void visit(ShowErrors node) {
        visitInternal(node.getLimit());
    }

    @Override
    public void visit(ShowEvents node) {
        visitInternal(node.getSchema());
        visitInternal(node.getWhere());
    }

    @Override
    public void visit(ShowFunctionCode node) {
        visitInternal(node.getFunctionName());
    }

    @Override
    public void visit(ShowFunctionStatus node) {
        visitInternal(node.getWhere());
    }

    @Override
    public void visit(ShowGrants node) {
        visitInternal(node.getUser());
    }

    @Override
    public void visit(ShowIndex node) {
        visitInternal(node.getTable());
    }

    @Override
    public void visit(ShowMasterStatus node) {
    }

    @Override
    public void visit(ShowOpenTables node) {
        visitInternal(node.getSchema());
        visitInternal(node.getWhere());
    }

    @Override
    public void visit(ShowPlugins node) {
    }

    @Override
    public void visit(ShowPrivileges node) {
    }

    @Override
    public void visit(ShowProcedureCode node) {
        visitInternal(node.getProcedureName());
    }

    @Override
    public void visit(ShowProcedureStatus node) {
        visitInternal(node.getWhere());
    }

    @Override
    public void visit(ShowProcesslist node) {
    }

    @Override
    public void visit(ShowProfile node) {
        visitInternal(node.getForQuery());
        visitInternal(node.getLimit());
    }

    @Override
    public void visit(ShowProfiles node) {
    }

    @Override
    public void visit(ShowSlaveHosts node) {
    }

    @Override
    public void visit(ShowSlaveStatus node) {
    }

    @Override
    public void visit(ShowStatus node) {
        visitInternal(node.getWhere());
    }

    @Override
    public void visit(ShowTables node) {
        visitInternal(node.getSchema());
        visitInternal(node.getWhere());
    }

    @Override
    public void visit(ShowTableStatus node) {
        visitInternal(node.getDatabase());
        visitInternal(node.getWhere());
    }

    @Override
    public void visit(ShowTriggers node) {
        visitInternal(node.getSchema());
        visitInternal(node.getWhere());
    }

    @Override
    public void visit(ShowVariables node) {
        visitInternal(node.getWhere());
    }

    @Override
    public void visit(ShowWarnings node) {
        visitInternal(node.getLimit());
    }

    @Override
    public void visit(DescTableStatement node) {
        visitInternal(node.getTable());
    }

    @Override
    public void visit(DALSetStatement node) {
        visitInternal(node.getAssignmentList());
    }

    @Override
    public void visit(DALSetNamesStatement node) {
    }

    @Override
    public void visit(DALSetCharacterSetStatement node) {
    }

    @Override
    public void visit(DMLCallStatement node) {
        visitInternal(node.getArguments());
        visitInternal(node.getProcedure());
    }

    @Override
    public void visit(DMLDeleteStatement node) {
        visitInternal(node.getLimit());
        visitInternal(node.getOrderBy());
        visitInternal(node.getTableNames());
        visitInternal(node.getTableRefs());
        visitInternal(node.getWhereCondition());
    }

    @Override
    public void visit(DMLInsertStatement node) {
        visitInternal(node.getColumnNameList());
        visitInternal(node.getDuplicateUpdate());
        visitInternal(node.getRowList());
        visitInternal(node.getSelect());
        visitInternal(node.getTable());
    }

    @Override
    public void visit(DMLReplaceStatement node) {
        visitInternal(node.getColumnNameList());
        visitInternal(node.getRowList());
        visitInternal(node.getSelect());
        visitInternal(node.getTable());
    }

    @Override
    public void visit(DMLSelectStatement node) {
        visitInternal(node.getGroup());
        visitInternal(node.getHaving());
        visitInternal(node.getLimit());
        visitInternal(node.getOrder());
        visitInternal(node.getSelectExprList());
        visitInternal(node.getTables());
        visitInternal(node.getWhere());
    }

    @Override
    public void visit(DMLSelectUnionStatement node) {
        visitInternal(node.getLimit());
        visitInternal(node.getOrderBy());
        visitInternal(node.getSelectStmtList());
    }

    @Override
    public void visit(DMLUpdateStatement node) {
        visitInternal(node.getLimit());
        visitInternal(node.getOrderBy());
        visitInternal(node.getTableRefs());
        visitInternal(node.getValues());
        visitInternal(node.getWhere());
    }

    @Override
    public void visit(MTSSetTransactionStatement node) {
    }

    @Override
    public void visit(MTSSavepointStatement node) {
        visitInternal(node.getSavepoint());
    }

    @Override
    public void visit(MTSReleaseStatement node) {
        visitInternal(node.getSavepoint());
    }

    @Override
    public void visit(MTSRollbackStatement node) {
        visitInternal(node.getSavepoint());
    }

    @Override
    public void visit(DDLTruncateStatement node) {
        visitInternal(node.getTable());
    }

    @Override
    public void visit(DDLAlterTableStatement node) {
        visitInternal(node.getTable());
    }

    @Override
    public void visit(DDLCreateIndexStatement node) {
        visitInternal(node.getIndexName());
        visitInternal(node.getTable());
    }

    @Override
    public void visit(DDLCreateTableStatement node) {
        visitInternal(node.getTable());
    }

    @Override
    public void visit(DDLRenameTableStatement node) {
        visitInternal(node.getList());
    }

    @Override
    public void visit(DDLDropIndexStatement node) {
        visitInternal(node.getIndexName());
        visitInternal(node.getTable());
    }

    @Override
    public void visit(DDLDropTableStatement node) {
        visitInternal(node.getTableNames());
    }

    @Override
    public void visit(ExtDDLCreatePolicy node) {
    }

    @Override
    public void visit(ExtDDLDropPolicy node) {
    }

}
