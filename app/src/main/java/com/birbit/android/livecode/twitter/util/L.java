package com.birbit.android.livecode.twitter.util;

import android.util.Log;


/**
 * This class is copied from roboguice source code to provide reasonable looging in xmpp project.
 */
@SuppressWarnings({"ImplicitArrayToString"})
public class L  {
    /**
     * config is initially set to BaseConfig() with sensible defaults, then replaced
     * by BaseConfig(ContextSingleton) during guice static injection pass.
     */
    protected static BaseConfig config = new BaseConfig();

    /**
     * print is initially set to Print(), then replaced by guice during
     * static injection pass.  This allows overriding where the log message is delivered to.
     */
    protected static Print print = new LogPrint();



    private L() {}



    public static int v(Throwable t) {
        return config.minimumLogLevel <= Log.VERBOSE ? print.println(Log.VERBOSE, Log.getStackTraceString(t)) : 0;
    }

    public static int v(Object s1, Object... args) {
        if( config.minimumLogLevel > Log.VERBOSE )
            return 0;

        final String s = convertToString(s1);
        final String message = args.length>0 ? String.format(s,args) : s;
        return print.println(Log.VERBOSE, message);
    }

    public static int v(Throwable throwable, Object s1, Object... args ) {
        if( config.minimumLogLevel > Log.VERBOSE )
            return 0;

        final String s = convertToString(s1);
        final String message = (args.length>0 ? String.format(s,args) : s) + '\n' + Log.getStackTraceString(throwable);
        return print.println(Log.VERBOSE, message);
    }

    public static int d(Throwable t) {
        return config.minimumLogLevel <= Log.DEBUG ? print.println(Log.DEBUG, Log.getStackTraceString(t)) : 0;
    }

    public static int d(Object s1, Object... args) {
        if( config.minimumLogLevel > Log.DEBUG )
            return 0;

        final String s = convertToString(s1);
        final String message = args.length>0 ? String.format(s,args) : s;
        return print.println(Log.DEBUG, message);
    }

    public static int d(Throwable throwable, Object s1, Object... args) {
        if( config.minimumLogLevel > Log.DEBUG )
            return 0;

        final String s = convertToString(s1);
        final String message = (args.length>0 ? String.format(s,args) : s) + '\n' + Log.getStackTraceString(throwable);
        return print.println(Log.DEBUG, message);
    }

    public static int i(Throwable t) {
        return config.minimumLogLevel <= Log.INFO ? print.println(Log.INFO, Log.getStackTraceString(t)) : 0;
    }

    public static int i( Object s1, Object... args) {
        if( config.minimumLogLevel > Log.INFO )
            return 0;

        final String s = convertToString(s1);
        final String message = args.length>0 ? String.format(s,args) : s;
        return print.println(Log.INFO, message);
    }

    public static int i(Throwable throwable, Object s1, Object... args) {
        if( config.minimumLogLevel > Log.INFO )
            return 0;

        final String s = convertToString(s1);
        final String message = (args.length > 0 ? String.format(s, args) : s) + '\n' + Log.getStackTraceString(throwable);
        return print.println(Log.INFO, message);
    }

    public static int w(Throwable t) {
        return config.minimumLogLevel <= Log.WARN ? print.println(Log.WARN, Log.getStackTraceString(t)) : 0;
    }

    public static int w( Object s1, Object... args) {
        if( config.minimumLogLevel > Log.WARN )
            return 0;

        final String s = convertToString(s1);
        final String message = args.length>0 ? String.format(s,args) : s;
        return print.println(Log.WARN, message);
    }

    public static int w( Throwable throwable, Object s1, Object... args) {
        if( config.minimumLogLevel > Log.WARN )
            return 0;

        final String s = convertToString(s1);
        final String message = (args.length>0 ? String.format(s,args) : s) + '\n' + Log.getStackTraceString(throwable);
        return print.println(Log.WARN, message);
    }

    public static int e(Throwable t) {
        return config.minimumLogLevel <= Log.ERROR ? print.println(Log.ERROR, Log.getStackTraceString(t)) : 0;
    }

    public static int e( Object s1, Object... args) {
        if( config.minimumLogLevel > Log.ERROR )
            return 0;

        final String s = convertToString(s1);
        final String message = args.length>0 ? String.format(s,args) : s;
        return print.println(Log.ERROR, message);
    }

    public static int e( Throwable throwable, Object s1, Object... args) {
        if( config.minimumLogLevel > Log.ERROR )
            return 0;

        final String s = convertToString(s1);
        final String message = (args.length>0 ? String.format(s,args) : s) + '\n' + Log.getStackTraceString(throwable);
        return print.println(Log.ERROR, message);
    }

    public static boolean isDebugEnabled() {
        return config.minimumLogLevel <= Log.DEBUG;
    }

    public static boolean isVerboseEnabled() {
        return config.minimumLogLevel <= Log.VERBOSE;
    }

    public static Config getConfig() {
        return config;
    }


    public static interface Config {
        public int getLoggingLevel();
        public void setLoggingLevel(int level);
    }

    public static interface Print {
        int println(int priority, String msg);
    }

    public static void setApplicationData(Boolean isMainApp) {
        //we keep these vars so that debugging will be easier

        config.mainAppString = isMainApp == null ? "null" : Boolean.toString(isMainApp);
        config.processId = android.os.Process.myPid();
        config.scope = config.scope + "( main app:" + config.mainAppString + ", pid:" + config.processId + ")";
    }

    public static class BaseConfig implements Config {
        protected int minimumLogLevel = Log.VERBOSE;
        protected String packageName = "com.path";
        protected String scope = "COM.PATH";
        protected long processId = -1;
        protected String mainAppString;

        protected BaseConfig() {
            minimumLogLevel = Log.ASSERT;
        }

        public int getLoggingLevel() {
            return minimumLogLevel;
        }

        public void setLoggingLevel(int level) {
            minimumLogLevel = level;
        }
    }

    public static String logLevelToString(int loglevel) {
        switch( loglevel ) {
            case Log.VERBOSE:
                return "VERBOSE";
            case Log.DEBUG:
                return "DEBUG";
            case Log.INFO:
                return "INFO";
            case Log.WARN:
                return "WARN";
            case Log.ERROR:
                return "ERROR";
            case Log.ASSERT:
                return "ASSERT";
        }

        return "UNKNOWN";
    }


    protected static String processMessage(String msg) {
        if( config.minimumLogLevel <= Log.DEBUG )
            msg = String.format("%s %s", Thread.currentThread().getName(), msg);
        return msg;
    }

    protected static String getScope(int skipDepth) {
        if( config.minimumLogLevel <= Log.DEBUG ) {
            final StackTraceElement trace = Thread.currentThread().getStackTrace()[skipDepth];
            return config.scope + "(tid: "+ Thread.currentThread().getId() + ")" + "/" + trace.getFileName() + ":" + trace.getLineNumber();
        }

        return config.scope;
    }

    private static String convertToString(Object o) {
        return o == null ? "" : o.toString();
    }


    /** Default implementation logs to android.util.Log */
    public static class LogPrint implements Print {

        @Override
        public int println(int priority, String msg) {
            return Log.println(priority, L.getScope(5), L.processMessage(msg));
        }

    }
}
