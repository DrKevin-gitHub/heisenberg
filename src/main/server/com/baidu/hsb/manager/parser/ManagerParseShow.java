/**
 * Baidu.com,Inc.
 * Copyright (c) 2000-2013 All Rights Reserved.
 */
package com.baidu.hsb.manager.parser;

import com.baidu.hsb.parser.util.ParseUtil;

/**
 * @author xiongzhao@baidu.com 2011-5-7 下午02:02:27
 */
public final class ManagerParseShow {

    public static final int OTHER = -1;
    public static final int COMMAND = 1;
    public static final int CONNECTION = 2;
    public static final int DATABASE = 3;
    public static final int DATANODE = 4;
    public static final int DATASOURCE = 5;
    public static final int HELP = 6;
    public static final int PARSER = 7;
    public static final int PROCESSOR = 8;
    public static final int ROUTER = 9;
    public static final int SERVER = 10;
    public static final int SQL = 11;
    public static final int SQL_DETAIL = 12;
    public static final int SQL_EXECUTE = 13;
    public static final int SQL_SLOW = 14;
    public static final int THREADPOOL = 15;
    public static final int TIME_CURRENT = 16;
    public static final int TIME_STARTUP = 17;
    public static final int VERSION = 18;
    public static final int VARIABLES = 19;
    public static final int COLLATION = 20;
    public static final int CONNECTION_SQL = 21;
    public static final int DATANODE_WHERE = 22;
    public static final int DATASOURCE_WHERE = 23;
    public static final int HEARTBEAT = 24;
    public static final int SLOW_DATANODE = 25;
    public static final int SLOW_SCHEMA = 26;
    public static final int BACKEND = 27;

    public static int parse(String stmt, int offset) {
        int i = offset;
        for (; i < stmt.length(); i++) {
            switch (stmt.charAt(i)) {
            case ' ':
                continue;
            case '/':
            case '#':
                i = ParseUtil.comment(stmt, i);
                continue;
            case '@':
                return show2Check(stmt, i);
            case 'C':
            case 'c':
                return showCCheck(stmt, i);
            case 'V':
            case 'v':
                return showVCheck(stmt, i);
            default:
                return OTHER;
            }
        }
        return OTHER;
    }

    // SHOW @
    static int show2Check(String stmt, int offset) {
        if (stmt.length() > ++offset && stmt.charAt(offset) == '@') {
            if (stmt.length() > ++offset) {
                switch (stmt.charAt(offset)) {
                case 'B':
                case 'b':
                    return show2BCheck(stmt, offset);
                case 'C':
                case 'c':
                    return show2CCheck(stmt, offset);
                case 'D':
                case 'd':
                    return show2DCheck(stmt, offset);
                case 'H':
                case 'h':
                    return show2HCheck(stmt, offset);
                case 'P':
                case 'p':
                    return show2PCheck(stmt, offset);
                case 'R':
                case 'r':
                    return show2RCheck(stmt, offset);
                case 'S':
                case 's':
                    return show2SCheck(stmt, offset);
                case 'T':
                case 't':
                    return show2TCheck(stmt, offset);
                case 'V':
                case 'v':
                    return show2VCheck(stmt, offset);
                default:
                    return OTHER;
                }
            }
        }
        return OTHER;
    }

    // SHOW COLLATION
    static int showCCheck(String stmt, int offset) {
        if (stmt.length() > offset + "OLLATION".length()) {
            char c1 = stmt.charAt(++offset);
            char c2 = stmt.charAt(++offset);
            char c3 = stmt.charAt(++offset);
            char c4 = stmt.charAt(++offset);
            char c5 = stmt.charAt(++offset);
            char c6 = stmt.charAt(++offset);
            char c7 = stmt.charAt(++offset);
            char c8 = stmt.charAt(++offset);
            if ((c1 == 'O' || c1 == 'o') && (c2 == 'L' || c2 == 'l') && (c3 == 'L' || c3 == 'l')
                    && (c4 == 'A' || c4 == 'a') && (c5 == 'T' || c5 == 't') && (c6 == 'I' || c6 == 'i')
                    && (c7 == 'O' || c7 == 'o') && (c8 == 'N' || c8 == 'n')) {
                if (stmt.length() > ++offset && stmt.charAt(offset) != ' ') {
                    return OTHER;
                }
                return COLLATION;
            }
        }
        return OTHER;
    }

    // SHOW VARIABLES
    static int showVCheck(String stmt, int offset) {
        if (stmt.length() > offset + "ARIABLES".length()) {
            char c1 = stmt.charAt(++offset);
            char c2 = stmt.charAt(++offset);
            char c3 = stmt.charAt(++offset);
            char c4 = stmt.charAt(++offset);
            char c5 = stmt.charAt(++offset);
            char c6 = stmt.charAt(++offset);
            char c7 = stmt.charAt(++offset);
            char c8 = stmt.charAt(++offset);
            if ((c1 == 'A' || c1 == 'a') && (c2 == 'R' || c2 == 'r') && (c3 == 'I' || c3 == 'i')
                    && (c4 == 'A' || c4 == 'a') && (c5 == 'B' || c5 == 'b') && (c6 == 'L' || c6 == 'l')
                    && (c7 == 'E' || c7 == 'e') && (c8 == 'S' || c8 == 's')) {
                if (stmt.length() > ++offset && stmt.charAt(offset) != ' ') {
                    return OTHER;
                }
                return VARIABLES;
            }
        }
        return OTHER;
    }

    // SHOW @@BACKEND
    static int show2BCheck(String stmt, int offset) {
        if (stmt.length() > offset + "ACKEND".length()) {
            char c1 = stmt.charAt(++offset);
            char c2 = stmt.charAt(++offset);
            char c3 = stmt.charAt(++offset);
            char c4 = stmt.charAt(++offset);
            char c5 = stmt.charAt(++offset);
            char c6 = stmt.charAt(++offset);
            if ((c1 == 'A' || c1 == 'a') && (c2 == 'C' || c2 == 'c') && (c3 == 'K' || c3 == 'k')
                    && (c4 == 'E' || c4 == 'e') && (c5 == 'N' || c5 == 'n') && (c6 == 'D' || c6 == 'd')
                    && (stmt.length() == ++offset || ParseUtil.isEOF(stmt.charAt(offset)))) {
                return BACKEND;
            }
        }
        return OTHER;
    }

    // SHOW @@C
    static int show2CCheck(String stmt, int offset) {
        if (stmt.length() > ++offset) {
            switch (stmt.charAt(offset)) {
            case 'O':
            case 'o':
                return show2CoCheck(stmt, offset);
            default:
                return OTHER;
            }
        }
        return OTHER;
    }

    // SHOW @@DATA
    static int show2DCheck(String stmt, int offset) {
        if (stmt.length() > offset + "ATA".length()) {
            char c1 = stmt.charAt(++offset);
            char c2 = stmt.charAt(++offset);
            char c3 = stmt.charAt(++offset);
            if ((c1 == 'A' || c1 == 'a') && (c2 == 'T' || c2 == 't') && (c3 == 'A' || c3 == 'a')) {
                if (stmt.length() > ++offset) {
                    switch (stmt.charAt(offset)) {
                    case 'B':
                    case 'b':
                        return show2DataBCheck(stmt, offset);
                    case 'N':
                    case 'n':
                        return show2DataNCheck(stmt, offset);
                    case 'S':
                    case 's':
                        return show2DataSCheck(stmt, offset);
                    default:
                        return OTHER;
                    }
                }
            }
        }
        return OTHER;
    }

    // SHOW @@HELP
    static int show2HCheck(String stmt, int offset) {
        if (stmt.length() > ++offset) {
            switch (stmt.charAt(offset)) {
            case 'E':
            case 'e':
                return show2HeCheck(stmt, offset);
            default:
                return OTHER;
            }
        }
        return OTHER;
    }

    // SHOW @@HE
    static int show2HeCheck(String stmt, int offset) {
        if (stmt.length() > ++offset) {
            switch (stmt.charAt(offset)) {
            case 'L':
            case 'l':
                return show2HelCheck(stmt, offset);
            case 'A':
            case 'a':
                return show2HeaCheck(stmt, offset);
            default:
                return OTHER;
            }
        }
        return OTHER;
    }

    // SHOW @@HELP
    static int show2HelCheck(String stmt, int offset) {
        if (stmt.length() > offset + "P".length()) {
            char c1 = stmt.charAt(++offset);
            if ((c1 == 'P' || c1 == 'p')) {
                if (stmt.length() > ++offset && stmt.charAt(offset) != ' ') {
                    return OTHER;
                }
                return HELP;
            }
        }
        return OTHER;
    }

    // SHOW @@HEARTBEAT
    static int show2HeaCheck(String stmt, int offset) {
        if (stmt.length() > offset + "RTBEAT".length()) {
            char c1 = stmt.charAt(++offset);
            char c2 = stmt.charAt(++offset);
            char c3 = stmt.charAt(++offset);
            char c4 = stmt.charAt(++offset);
            char c5 = stmt.charAt(++offset);
            char c6 = stmt.charAt(++offset);
            if ((c1 == 'R' || c1 == 'r') && (c2 == 'T' || c2 == 't') & (c3 == 'B' || c3 == 'b')
                    && (c4 == 'E' || c4 == 'e') & (c5 == 'A' || c5 == 'a') && (c6 == 'T' || c6 == 't')) {
                if (stmt.length() > ++offset && stmt.charAt(offset) != ' ') {
                    return OTHER;
                }
                return HEARTBEAT;
            }
        }
        return OTHER;
    }

    // SHOW @@P
    static int show2PCheck(String stmt, int offset) {
        if (stmt.length() > ++offset) {
            switch (stmt.charAt(offset)) {
            case 'A':
            case 'a':
                return show2PaCheck(stmt, offset);
            case 'R':
            case 'r':
                return show2PrCheck(stmt, offset);
            default:
                return OTHER;
            }
        }
        return OTHER;
    }

    // SHOW @@ROUTER
    static int show2RCheck(String stmt, int offset) {
        if (stmt.length() > offset + "OUTER".length()) {
            char c1 = stmt.charAt(++offset);
            char c2 = stmt.charAt(++offset);
            char c3 = stmt.charAt(++offset);
            char c4 = stmt.charAt(++offset);
            char c5 = stmt.charAt(++offset);
            if ((c1 == 'O' || c1 == 'o') && (c2 == 'U' || c2 == 'u') && (c3 == 'T' || c3 == 't')
                    && (c4 == 'E' || c4 == 'e') && (c5 == 'R' || c5 == 'r')) {
                if (stmt.length() > ++offset && stmt.charAt(offset) != ' ') {
                    return OTHER;
                }
                return ROUTER;
            }
        }
        return OTHER;
    }

    // SHOW @@S
    static int show2SCheck(String stmt, int offset) {
        if (stmt.length() > ++offset) {
            switch (stmt.charAt(offset)) {
            case 'E':
            case 'e':
                return show2SeCheck(stmt, offset);
            case 'Q':
            case 'q':
                return show2SqCheck(stmt, offset);
            case 'L':
            case 'l':
                return show2SlCheck(stmt, offset);
            default:
                return OTHER;
            }
        }
        return OTHER;
    }

    // SHOW @@SLOW
    static int show2SlCheck(String stmt, int offset) {
        if (stmt.length() > offset + "OW ".length()) {
            char c1 = stmt.charAt(++offset);
            char c2 = stmt.charAt(++offset);
            char c3 = stmt.charAt(++offset);
            if ((c1 == 'O' || c1 == 'o') && (c2 == 'W' || c2 == 'w') && c3 == ' ') {
                while (stmt.length() > ++offset) {
                    switch (stmt.charAt(offset)) {
                    case ' ':
                        continue;
                    case 'W':
                    case 'w':
                        return show2SlowWhereCheck(stmt, offset);
                    default:
                        return OTHER;
                    }
                }
            }
        }
        return OTHER;
    }

    // SHOW @@SLOW WHERE
    static int show2SlowWhereCheck(String stmt, int offset) {
        if (stmt.length() > offset + "HERE".length()) {
            char c1 = stmt.charAt(++offset);
            char c2 = stmt.charAt(++offset);
            char c3 = stmt.charAt(++offset);
            char c4 = stmt.charAt(++offset);
            if ((c1 == 'H' || c1 == 'h') && (c2 == 'E' || c2 == 'e') && (c3 == 'R' || c3 == 'r')
                    && (c4 == 'E' || c4 == 'e')) {
                while (stmt.length() > ++offset) {
                    switch (stmt.charAt(offset)) {
                    case ' ':
                        continue;
                    case 'D':
                    case 'd':
                        return show2SlowWhereDCheck(stmt, offset);
                    case 'S':
                    case 's':
                        return show2SlowWhereSCheck(stmt, offset);
                    default:
                        return OTHER;
                    }
                }
            }
        }
        return OTHER;
    }

    // SHOW @@SLOW WHERE DATANODE= XXXXXX
    static int show2SlowWhereDCheck(String stmt, int offset) {
        if (stmt.length() > offset + "ATANODE".length()) {
            char c1 = stmt.charAt(++offset);
            char c2 = stmt.charAt(++offset);
            char c3 = stmt.charAt(++offset);
            char c4 = stmt.charAt(++offset);
            char c5 = stmt.charAt(++offset);
            char c6 = stmt.charAt(++offset);
            char c7 = stmt.charAt(++offset);
            if ((c1 == 'A' || c1 == 'a') && (c2 == 'T' || c2 == 't') && (c3 == 'A' || c3 == 'a')
                    && (c4 == 'N' || c4 == 'n') && (c5 == 'O' || c5 == 'o') && (c6 == 'D' || c6 == 'd')
                    && (c7 == 'E' || c7 == 'e')) {
                while (stmt.length() > ++offset) {
                    switch (stmt.charAt(offset)) {
                    case ' ':
                        continue;
                    case '=':
                        while (stmt.length() > ++offset) {
                            switch (stmt.charAt(offset)) {
                            case ' ':
                                continue;
                            default:
                                return (offset << 8) | SLOW_DATANODE;
                            }
                        }
                        return OTHER;
                    default:
                        return OTHER;
                    }
                }
            }
        }
        return OTHER;
    }

    // SHOW @@SLOW WHERE SCHEMA= XXXXXX
    static int show2SlowWhereSCheck(String stmt, int offset) {
        if (stmt.length() > offset + "CHEMA".length()) {
            char c1 = stmt.charAt(++offset);
            char c2 = stmt.charAt(++offset);
            char c3 = stmt.charAt(++offset);
            char c4 = stmt.charAt(++offset);
            char c5 = stmt.charAt(++offset);
            if ((c1 == 'C' || c1 == 'c') && (c2 == 'H' || c2 == 'h') && (c3 == 'E' || c3 == 'e')
                    && (c4 == 'M' || c4 == 'm') && (c5 == 'A' || c5 == 'a')) {
                while (stmt.length() > ++offset) {
                    switch (stmt.charAt(offset)) {
                    case ' ':
                        continue;
                    case '=':
                        while (stmt.length() > ++offset) {
                            switch (stmt.charAt(offset)) {
                            case ' ':
                                continue;
                            default:
                                return (offset << 8) | SLOW_SCHEMA;
                            }
                        }
                        return OTHER;
                    default:
                        return OTHER;
                    }
                }
            }
        }
        return OTHER;
    }

    // SHOW @@T
    static int show2TCheck(String stmt, int offset) {
        if (stmt.length() > ++offset) {
            switch (stmt.charAt(offset)) {
            case 'H':
            case 'h':
                return show2ThCheck(stmt, offset);
            case 'I':
            case 'i':
                return show2TiCheck(stmt, offset);
            default:
                return OTHER;
            }
        }
        return OTHER;
    }

    // SHOW @@VERSION
    static int show2VCheck(String stmt, int offset) {
        if (stmt.length() > offset + "ERSION".length()) {
            char c1 = stmt.charAt(++offset);
            char c2 = stmt.charAt(++offset);
            char c3 = stmt.charAt(++offset);
            char c4 = stmt.charAt(++offset);
            char c5 = stmt.charAt(++offset);
            char c6 = stmt.charAt(++offset);
            if ((c1 == 'E' || c1 == 'e') && (c2 == 'R' || c2 == 'r') && (c3 == 'S' || c3 == 's')
                    && (c4 == 'I' || c4 == 'i') && (c5 == 'O' || c5 == 'o') && (c6 == 'N' || c6 == 'n')) {
                if (stmt.length() > ++offset && stmt.charAt(offset) != ' ') {
                    return OTHER;
                }
                return VERSION;
            }
        }
        return OTHER;
    }

    // SHOW @@CO
    static int show2CoCheck(String stmt, int offset) {
        if (stmt.length() > ++offset) {
            switch (stmt.charAt(offset)) {
            case 'M':
            case 'm':
                return show2ComCheck(stmt, offset);
            case 'N':
            case 'n':
                return show2ConCheck(stmt, offset);
            default:
                return OTHER;
            }
        }
        return OTHER;
    }

    // SHOW @@DATABASE
    static int show2DataBCheck(String stmt, int offset) {
        if (stmt.length() > offset + "ASE".length()) {
            char c1 = stmt.charAt(++offset);
            char c2 = stmt.charAt(++offset);
            char c3 = stmt.charAt(++offset);
            if ((c1 == 'A' || c1 == 'a') && (c2 == 'S' || c2 == 's') && (c3 == 'E' || c3 == 'e')) {
                if (stmt.length() > ++offset && stmt.charAt(offset) != ' ') {
                    return OTHER;
                }
                return DATABASE;
            }
        }
        return OTHER;
    }

    // SHOW @@DATANODE
    static int show2DataNCheck(String stmt, int offset) {
        if (stmt.length() > offset + "ODE".length()) {
            char c1 = stmt.charAt(++offset);
            char c2 = stmt.charAt(++offset);
            char c3 = stmt.charAt(++offset);
            if ((c1 == 'O' || c1 == 'o') && (c2 == 'D' || c2 == 'd') && (c3 == 'E' || c3 == 'e')) {
                while (stmt.length() > ++offset) {
                    switch (stmt.charAt(offset)) {
                    case ' ':
                        continue;
                    case 'W':
                    case 'w':
                        return show2DataNWhereCheck(stmt, offset);
                    default:
                        return OTHER;
                    }
                }
                return DATANODE;
            }
        }
        return OTHER;
    }

    // SHOW @@DATANODE WHERE
    static int show2DataNWhereCheck(String stmt, int offset) {
        if (stmt.length() > offset + "HERE".length()) {
            char c1 = stmt.charAt(++offset);
            char c2 = stmt.charAt(++offset);
            char c3 = stmt.charAt(++offset);
            char c4 = stmt.charAt(++offset);
            if ((c1 == 'H' || c1 == 'h') && (c2 == 'E' || c2 == 'e') && (c3 == 'R' || c3 == 'r')
                    && (c4 == 'E' || c4 == 'e')) {
                while (stmt.length() > ++offset) {
                    switch (stmt.charAt(offset)) {
                    case ' ':
                        continue;
                    case 'S':
                    case 's':
                        return show2DataNWhereSchemaCheck(stmt, offset);
                    default:
                        return OTHER;
                    }
                }
            }
        }
        return OTHER;
    }

    // SHOW @@DATANODE WHERE SCHEMA = XXXXXX
    static int show2DataNWhereSchemaCheck(String stmt, int offset) {
        if (stmt.length() > offset + "CHEMA".length()) {
            char c1 = stmt.charAt(++offset);
            char c2 = stmt.charAt(++offset);
            char c3 = stmt.charAt(++offset);
            char c4 = stmt.charAt(++offset);
            char c5 = stmt.charAt(++offset);
            if ((c1 == 'C' || c1 == 'c') && (c2 == 'H' || c2 == 'h') && (c3 == 'E' || c3 == 'e')
                    && (c4 == 'M' || c4 == 'm') && (c5 == 'A' || c5 == 'a')) {
                while (stmt.length() > ++offset) {
                    switch (stmt.charAt(offset)) {
                    case ' ':
                        continue;
                    case '=':
                        while (stmt.length() > ++offset) {
                            switch (stmt.charAt(offset)) {
                            case ' ':
                                continue;
                            default:
                                return (offset << 8) | DATANODE_WHERE;
                            }
                        }
                        return OTHER;
                    default:
                        return OTHER;
                    }
                }
            }
        }
        return OTHER;
    }

    // SHOW @@DATASOURCE
    static int show2DataSCheck(String stmt, int offset) {
        if (stmt.length() > offset + "OURCE".length()) {
            char c1 = stmt.charAt(++offset);
            char c2 = stmt.charAt(++offset);
            char c3 = stmt.charAt(++offset);
            char c4 = stmt.charAt(++offset);
            char c5 = stmt.charAt(++offset);
            if ((c1 == 'O' || c1 == 'o') && (c2 == 'U' || c2 == 'u') && (c3 == 'R' || c3 == 'r')
                    && (c4 == 'C' || c4 == 'c') && (c5 == 'E' || c5 == 'e')) {
                while (stmt.length() > ++offset) {
                    switch (stmt.charAt(offset)) {
                    case ' ':
                        continue;
                    case 'W':
                    case 'w':
                        return show2DataSWhereCheck(stmt, offset);
                    default:
                        return OTHER;
                    }
                }

                return DATASOURCE;
            }
        }
        return OTHER;
    }

    // SHOW @@DATASOURCE WHERE
    static int show2DataSWhereCheck(String stmt, int offset) {
        if (stmt.length() > offset + "HERE".length()) {
            char c1 = stmt.charAt(++offset);
            char c2 = stmt.charAt(++offset);
            char c3 = stmt.charAt(++offset);
            char c4 = stmt.charAt(++offset);
            if ((c1 == 'H' || c1 == 'h') && (c2 == 'E' || c2 == 'e') && (c3 == 'R' || c3 == 'r')
                    && (c4 == 'E' || c4 == 'e')) {
                while (stmt.length() > ++offset) {
                    switch (stmt.charAt(offset)) {
                    case ' ':
                        continue;
                    case 'd':
                    case 'D':
                        return show2DataSWhereDatanodeCheck(stmt, offset);
                    default:
                        return OTHER;
                    }
                }
            }
        }
        return OTHER;
    }

    // SHOW @@DATASOURCE WHERE DATANODE = XXXXXX
    static int show2DataSWhereDatanodeCheck(String stmt, int offset) {
        if (stmt.length() > offset + "ATANODE".length()) {
            char c1 = stmt.charAt(++offset);
            char c2 = stmt.charAt(++offset);
            char c3 = stmt.charAt(++offset);
            char c4 = stmt.charAt(++offset);
            char c5 = stmt.charAt(++offset);
            char c6 = stmt.charAt(++offset);
            char c7 = stmt.charAt(++offset);
            if ((c1 == 'A' || c1 == 'a') && (c2 == 'T' || c2 == 't') && (c3 == 'A' || c3 == 'a')
                    && (c4 == 'N' || c4 == 'n') && (c5 == 'O' || c5 == 'o') && (c6 == 'D' || c6 == 'd')
                    && (c7 == 'E' || c7 == 'e')) {
                while (stmt.length() > ++offset) {
                    switch (stmt.charAt(offset)) {
                    case ' ':
                        continue;
                    case '=':
                        while (stmt.length() > ++offset) {
                            switch (stmt.charAt(offset)) {
                            case ' ':
                                continue;
                            default:
                                return (offset << 8) | DATASOURCE_WHERE;
                            }
                        }
                        return OTHER;
                    default:
                        return OTHER;
                    }
                }
            }
        }
        return OTHER;
    }

    // SHOW @@PARSER
    static int show2PaCheck(String stmt, int offset) {
        if (stmt.length() > offset + "RSER".length()) {
            char c1 = stmt.charAt(++offset);
            char c2 = stmt.charAt(++offset);
            char c3 = stmt.charAt(++offset);
            char c4 = stmt.charAt(++offset);
            if ((c1 == 'R' || c1 == 'r') && (c2 == 'S' || c2 == 's') && (c3 == 'E' || c3 == 'e')
                    && (c4 == 'R' || c4 == 'r')) {
                if (stmt.length() > ++offset && stmt.charAt(offset) != ' ') {
                    return OTHER;
                }
                return PARSER;
            }
        }
        return OTHER;
    }

    // SHOW @@PROCESSOR
    static int show2PrCheck(String stmt, int offset) {
        if (stmt.length() > offset + "OCESSOR".length()) {
            char c1 = stmt.charAt(++offset);
            char c2 = stmt.charAt(++offset);
            char c3 = stmt.charAt(++offset);
            char c4 = stmt.charAt(++offset);
            char c5 = stmt.charAt(++offset);
            char c6 = stmt.charAt(++offset);
            char c7 = stmt.charAt(++offset);
            if ((c1 == 'O' || c1 == 'o') && (c2 == 'C' || c2 == 'c') && (c3 == 'E' || c3 == 'e')
                    && (c4 == 'S' || c4 == 's') && (c5 == 'S' || c5 == 's') && (c6 == 'O' || c6 == 'o')
                    && (c7 == 'R' || c7 == 'r')) {
                if (stmt.length() > ++offset && stmt.charAt(offset) != ' ') {
                    return OTHER;
                }
                return PROCESSOR;
            }
        }
        return OTHER;
    }

    // SHOW @@SERVER
    static int show2SeCheck(String stmt, int offset) {
        if (stmt.length() > offset + "RVER".length()) {
            char c1 = stmt.charAt(++offset);
            char c2 = stmt.charAt(++offset);
            char c3 = stmt.charAt(++offset);
            char c4 = stmt.charAt(++offset);
            if ((c1 == 'R' || c1 == 'r') && (c2 == 'V' || c2 == 'v') && (c3 == 'E' || c3 == 'e')
                    && (c4 == 'R' || c4 == 'r')) {
                if (stmt.length() > ++offset && stmt.charAt(offset) != ' ') {
                    return OTHER;
                }
                return SERVER;
            }
        }
        return OTHER;
    }

    // SHOW @@THREADPOOL
    static int show2ThCheck(String stmt, int offset) {
        if (stmt.length() > offset + "READPOOL".length()) {
            char c1 = stmt.charAt(++offset);
            char c2 = stmt.charAt(++offset);
            char c3 = stmt.charAt(++offset);
            char c4 = stmt.charAt(++offset);
            char c5 = stmt.charAt(++offset);
            char c6 = stmt.charAt(++offset);
            char c7 = stmt.charAt(++offset);
            char c8 = stmt.charAt(++offset);
            if ((c1 == 'R' || c1 == 'r') && (c2 == 'E' || c2 == 'e') && (c3 == 'A' || c3 == 'a')
                    && (c4 == 'D' || c4 == 'd') && (c5 == 'P' || c5 == 'p') && (c6 == 'O' || c6 == 'o')
                    && (c7 == 'O' || c7 == 'o') && (c8 == 'L' || c8 == 'l')) {
                if (stmt.length() > ++offset && stmt.charAt(offset) != ' ') {
                    return OTHER;
                }
                return THREADPOOL;
            }
        }
        return OTHER;
    }

    // SHOW @@TIME.
    static int show2TiCheck(String stmt, int offset) {
        if (stmt.length() > offset + "ME.".length()) {
            char c1 = stmt.charAt(++offset);
            char c2 = stmt.charAt(++offset);
            char c3 = stmt.charAt(++offset);
            if ((c1 == 'M' || c1 == 'm') && (c2 == 'E' || c2 == 'e') && (c3 == '.')) {
                if (stmt.length() > ++offset) {
                    switch (stmt.charAt(offset)) {
                    case 'C':
                    case 'c':
                        return show2TimeCCheck(stmt, offset);
                    case 'S':
                    case 's':
                        return show2TimeSCheck(stmt, offset);
                    default:
                        return OTHER;
                    }
                }
            }
        }
        return OTHER;
    }

    // SHOW @@COMMAND
    static int show2ComCheck(String stmt, int offset) {
        if (stmt.length() > offset + "MAND".length()) {
            char c1 = stmt.charAt(++offset);
            char c2 = stmt.charAt(++offset);
            char c3 = stmt.charAt(++offset);
            char c4 = stmt.charAt(++offset);
            if ((c1 == 'M' || c1 == 'm') && (c2 == 'A' || c2 == 'a') && (c3 == 'N' || c3 == 'n')
                    && (c4 == 'D' || c4 == 'd')) {
                if (stmt.length() > ++offset && stmt.charAt(offset) != ' ') {
                    return OTHER;
                }
                return COMMAND;
            }
        }
        return OTHER;
    }

    // SHOW @@CONNECTION
    static int show2ConCheck(String stmt, int offset) {
        if (stmt.length() > offset + "NECTION".length()) {
            char c1 = stmt.charAt(++offset);
            char c2 = stmt.charAt(++offset);
            char c3 = stmt.charAt(++offset);
            char c4 = stmt.charAt(++offset);
            char c5 = stmt.charAt(++offset);
            char c6 = stmt.charAt(++offset);
            char c7 = stmt.charAt(++offset);
            if ((c1 == 'N' || c1 == 'n') && (c2 == 'E' || c2 == 'e') && (c3 == 'C' || c3 == 'c')
                    && (c4 == 'T' || c4 == 't') && (c5 == 'I' || c5 == 'i') && (c6 == 'O' || c6 == 'o')
                    && (c7 == 'N' || c7 == 'n')) {
                if (stmt.length() > ++offset) {
                    switch (stmt.charAt(offset)) {
                    case ' ':
                        return CONNECTION;
                    case '.':
                        return show2ConnectonSQL(stmt, offset);
                    default:
                        return OTHER;
                    }
                }
                return CONNECTION;
            }
        }
        return OTHER;
    }

    // SHOW @@CONNECTION.SQL
    static int show2ConnectonSQL(String stmt, int offset) {
        if (stmt.length() > offset + "SQL".length()) {
            char c1 = stmt.charAt(++offset);
            char c2 = stmt.charAt(++offset);
            char c3 = stmt.charAt(++offset);
            if ((c1 == 'S' || c1 == 's') && (c2 == 'Q' || c2 == 'q') && (c3 == 'L' || c3 == 'l')) {
                if (stmt.length() > ++offset && stmt.charAt(offset) != ' ') {
                    return OTHER;
                }
                return CONNECTION_SQL;
            }
        }
        return OTHER;
    }

    // SHOW @@TIME.CURRENT
    static int show2TimeCCheck(String stmt, int offset) {
        if (stmt.length() > offset + "URRENT".length()) {
            char c1 = stmt.charAt(++offset);
            char c2 = stmt.charAt(++offset);
            char c3 = stmt.charAt(++offset);
            char c4 = stmt.charAt(++offset);
            char c5 = stmt.charAt(++offset);
            char c6 = stmt.charAt(++offset);
            if ((c1 == 'U' || c1 == 'u') && (c2 == 'R' || c2 == 'r') && (c3 == 'R' || c3 == 'r')
                    && (c4 == 'E' || c4 == 'e') && (c5 == 'N' || c5 == 'n') && (c6 == 'T' || c6 == 't')) {
                if (stmt.length() > ++offset && stmt.charAt(offset) != ' ') {
                    return OTHER;
                }
                return TIME_CURRENT;
            }
        }
        return OTHER;
    }

    // SHOW @@TIME.STARTUP
    static int show2TimeSCheck(String stmt, int offset) {
        if (stmt.length() > offset + "TARTUP".length()) {
            char c1 = stmt.charAt(++offset);
            char c2 = stmt.charAt(++offset);
            char c3 = stmt.charAt(++offset);
            char c4 = stmt.charAt(++offset);
            char c5 = stmt.charAt(++offset);
            char c6 = stmt.charAt(++offset);
            if ((c1 == 'T' || c1 == 't') && (c2 == 'A' || c2 == 'a') && (c3 == 'R' || c3 == 'r')
                    && (c4 == 'T' || c4 == 't') && (c5 == 'U' || c5 == 'u') && (c6 == 'P' || c6 == 'p')) {
                if (stmt.length() > ++offset && stmt.charAt(offset) != ' ') {
                    return OTHER;
                }
                return TIME_STARTUP;
            }
        }
        return OTHER;
    }

    // SHOW @@SQ
    static int show2SqCheck(String stmt, int offset) {
        if (stmt.length() > ++offset) {
            switch (stmt.charAt(offset)) {
            case 'L':
            case 'l':
                return show2SqlCheck(stmt, offset);
            default:
                return OTHER;
            }
        }
        return OTHER;
    }

    // SHOW @@SQL
    static int show2SqlCheck(String stmt, int offset) {
        if (stmt.length() > ++offset) {
            switch (stmt.charAt(offset)) {
            case '.':
                return show2SqlDotCheck(stmt, offset);
            case ' ':
                return show2SqlBlankCheck(stmt, offset);
            default:
                return OTHER;
            }
        }
        return OTHER;
    }

    // SHOW @@SQL.
    static int show2SqlDotCheck(String stmt, int offset) {
        if (stmt.length() > ++offset) {
            switch (stmt.charAt(offset)) {
            case 'D':
            case 'd':
                return show2SqlDCheck(stmt, offset);
            case 'E':
            case 'e':
                return show2SqlECheck(stmt, offset);
            case 'S':
            case 's':
                return show2SqlSCheck(stmt, offset);
            default:
                return OTHER;
            }
        }
        return OTHER;
    }

    // SHOW @@SQL WHERE ID = XXXXXX
    static int show2SqlBlankCheck(String stmt, int offset) {
        for (++offset; stmt.length() > offset; ++offset) {
            switch (stmt.charAt(offset)) {
            case ' ':
                continue;
            case 'W':
            case 'w':
                if (isWhere(stmt, offset)) {
                    return SQL;
                } else {
                    return OTHER;
                }
            default:
                return OTHER;
            }
        }

        return OTHER;
    }

    // SHOW @@SQL.DETAIL WHERE ID = XXXXXX
    static int show2SqlDCheck(String stmt, int offset) {
        if (stmt.length() > offset + "ETAIL".length()) {
            char c1 = stmt.charAt(++offset);
            char c2 = stmt.charAt(++offset);
            char c3 = stmt.charAt(++offset);
            char c4 = stmt.charAt(++offset);
            char c5 = stmt.charAt(++offset);
            if ((c1 == 'E' || c1 == 'e') && (c2 == 'T' || c2 == 't') && (c3 == 'A' || c3 == 'a')
                    && (c4 == 'I' || c4 == 'i') && (c5 == 'L' || c5 == 'l')) {
                for (++offset; stmt.length() > offset; ++offset) {
                    switch (stmt.charAt(offset)) {
                    case ' ':
                        continue;
                    case 'W':
                    case 'w':
                        if (isWhere(stmt, offset)) {
                            return SQL_DETAIL;
                        } else {
                            return OTHER;
                        }
                    default:
                        return OTHER;
                    }
                }
            }
        }
        return OTHER;
    }

    // SHOW @@SQL.EXECUTE
    static int show2SqlECheck(String stmt, int offset) {
        if (stmt.length() > offset + "XECUTE".length()) {
            char c1 = stmt.charAt(++offset);
            char c2 = stmt.charAt(++offset);
            char c3 = stmt.charAt(++offset);
            char c4 = stmt.charAt(++offset);
            char c5 = stmt.charAt(++offset);
            char c6 = stmt.charAt(++offset);
            if ((c1 == 'X' || c1 == 'x') && (c2 == 'E' || c2 == 'e') && (c3 == 'C' || c3 == 'c')
                    && (c4 == 'U' || c4 == 'u') && (c5 == 'T' || c5 == 't') && (c6 == 'E' || c6 == 'e')) {
                if (stmt.length() > ++offset && stmt.charAt(offset) != ' ') {
                    return OTHER;
                }
                return SQL_EXECUTE;
            }
        }
        return OTHER;
    }

    // SHOW @@SQL.SLOW
    static int show2SqlSCheck(String stmt, int offset) {
        if (stmt.length() > offset + "LOW".length()) {
            char c1 = stmt.charAt(++offset);
            char c2 = stmt.charAt(++offset);
            char c3 = stmt.charAt(++offset);
            if ((c1 == 'L' || c1 == 'l') && (c2 == 'O' || c2 == 'o') && (c3 == 'W' || c3 == 'w')) {
                if (stmt.length() > ++offset && stmt.charAt(offset) != ' ') {
                    return OTHER;
                }
                return SQL_SLOW;
            }
        }
        return OTHER;
    }

    static boolean isWhere(String stmt, int offset) {
        if (stmt.length() > offset + 5) {
            char c1 = stmt.charAt(++offset);
            char c2 = stmt.charAt(++offset);
            char c3 = stmt.charAt(++offset);
            char c4 = stmt.charAt(++offset);
            char c5 = stmt.charAt(++offset);
            if ((c1 == 'H' || c1 == 'h') && (c2 == 'E' || c2 == 'e') && (c3 == 'R' || c3 == 'r')
                    && (c4 == 'E' || c4 == 'e') && (c5 == ' ')) {
                boolean jump1 = false;
                for (++offset; stmt.length() > offset && !jump1; ++offset) {
                    switch (stmt.charAt(offset)) {
                    case ' ':
                        continue;
                    case 'I':
                    case 'i':
                        jump1 = true;
                        break;
                    default:
                        return false;
                    }
                }
                if ((stmt.length() > offset) && (stmt.charAt(offset) == 'D' || stmt.charAt(offset) == 'd')) {
                    boolean jump2 = false;
                    for (++offset; stmt.length() > offset && !jump2; ++offset) {
                        switch (stmt.charAt(offset)) {
                        case ' ':
                            continue;
                        case '=':
                            jump2 = true;
                            break;
                        default:
                            return false;
                        }
                    }
                    return isSqlId(stmt, offset);
                }
            }
        }
        return false;
    }

    static boolean isSqlId(String stmt, int offset) {
        String id = stmt.substring(offset).trim();
        try {
            Long.parseLong(id);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static String getWhereParameter(String stmt) {
        int offset = stmt.indexOf('=');
        ++offset;
        return stmt.substring(offset).trim();
    }

}
