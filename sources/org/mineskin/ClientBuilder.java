/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 */
package org.mineskin;

import com.google.gson.Gson;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import org.mineskin.JobCheckOptions;
import org.mineskin.MineSkinClient;
import org.mineskin.MineSkinClientImpl;
import org.mineskin.QueueOptions;
import org.mineskin.RequestExecutors;
import org.mineskin.options.AutoGenerateQueueOptions;
import org.mineskin.options.IJobCheckOptions;
import org.mineskin.options.IQueueOptions;
import org.mineskin.request.RequestHandler;
import org.mineskin.request.RequestHandlerConstructor;

public class ClientBuilder {
    private static final int DEFAULT_GENERATE_QUEUE_INTERVAL = 200;
    private static final int DEFAULT_GENERATE_QUEUE_CONCURRENCY = 1;
    private static final int DEFAULT_GET_QUEUE_INTERVAL = 100;
    private static final int DEFAULT_GET_QUEUE_CONCURRENCY = 5;
    private static final int DEFAULT_JOB_CHECK_INTERVAL = 1000;
    private static final int DEFAULT_JOB_CHECK_INITIAL_DELAY = 2000;
    private static final int DEFAULT_JOB_CHECK_MAX_ATTEMPTS = 10;
    private String baseUrl = "https://api.mineskin.org";
    private String userAgent = "MineSkinClient";
    private String apiKey = null;
    private int timeout = 10000;
    private Gson gson = new Gson();
    private Executor getExecutor = null;
    private Executor generateExecutor = null;
    private IQueueOptions generateQueueOptions = null;
    private IQueueOptions getQueueOptions = null;
    private IJobCheckOptions jobCheckOptions = null;
    private RequestHandlerConstructor requestHandlerConstructor = null;

    private ClientBuilder() {
    }

    public static ClientBuilder create() {
        return new ClientBuilder();
    }

    public ClientBuilder baseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    public ClientBuilder userAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public ClientBuilder apiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    public ClientBuilder timeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public ClientBuilder gson(Gson gson) {
        this.gson = gson;
        return this;
    }

    public ClientBuilder getExecutor(Executor getExecutor) {
        this.getExecutor = getExecutor;
        return this;
    }

    public ClientBuilder generateExecutor(Executor generateExecutor) {
        this.generateExecutor = generateExecutor;
        return this;
    }

    @Deprecated
    public ClientBuilder generateRequestScheduler(ScheduledExecutorService scheduledExecutor) {
        this.generateQueueOptions = new QueueOptions(scheduledExecutor, 200, 1);
        return this;
    }

    public ClientBuilder generateQueueOptions(IQueueOptions queueOptions) {
        this.generateQueueOptions = queueOptions;
        return this;
    }

    @Deprecated
    public ClientBuilder getRequestScheduler(ScheduledExecutorService scheduledExecutor) {
        this.getQueueOptions = new QueueOptions(scheduledExecutor, 100, 5);
        return this;
    }

    public ClientBuilder getQueueOptions(IQueueOptions queueOptions) {
        this.getQueueOptions = queueOptions;
        return this;
    }

    @Deprecated
    public ClientBuilder jobCheckScheduler(ScheduledExecutorService scheduledExecutor) {
        this.jobCheckOptions = new JobCheckOptions(scheduledExecutor, 1000, 2000, 10, false);
        return this;
    }

    public ClientBuilder jobCheckOptions(IJobCheckOptions jobCheckOptions) {
        this.jobCheckOptions = jobCheckOptions;
        return this;
    }

    public ClientBuilder requestHandler(RequestHandlerConstructor requestHandlerConstructor) {
        this.requestHandlerConstructor = requestHandlerConstructor;
        return this;
    }

    public MineSkinClient build() {
        String[] split;
        if (this.requestHandlerConstructor == null) {
            throw new IllegalStateException("RequestHandlerConstructor is not set");
        }
        if ("MineSkinClient".equals(this.userAgent)) {
            MineSkinClientImpl.LOGGER.log(Level.WARNING, "Using default User-Agent: MineSkinClient - Please set a custom User-Agent (e.g. AppName/Version) to identify your application");
        }
        if (this.apiKey == null || this.apiKey.isBlank()) {
            this.apiKey = null;
            MineSkinClientImpl.LOGGER.log(Level.WARNING, "Creating MineSkinClient without API key - Please get an API key from https://account.mineskin.org/keys");
        } else if (this.apiKey.startsWith("msk_") && (split = this.apiKey.split("_", 3)).length == 3) {
            String id = split[1];
            MineSkinClientImpl.LOGGER.log(Level.FINE, "Creating MineSkinClient with API key: " + id);
        }
        if (this.getExecutor == null) {
            this.getExecutor = Executors.newSingleThreadExecutor(r -> {
                Thread thread = new Thread(r);
                thread.setName("MineSkinClient/get");
                return thread;
            });
        }
        if (this.generateExecutor == null) {
            this.generateExecutor = Executors.newSingleThreadExecutor(r -> {
                Thread thread = new Thread(r);
                thread.setName("MineSkinClient/generate");
                return thread;
            });
        }
        if (this.generateQueueOptions == null) {
            this.generateQueueOptions = new QueueOptions(Executors.newSingleThreadScheduledExecutor(r -> {
                Thread thread = new Thread(r);
                thread.setName("MineSkinClient/scheduler");
                return thread;
            }), 200, 1);
        }
        if (this.getQueueOptions == null) {
            this.getQueueOptions = new QueueOptions(this.generateQueueOptions.scheduler(), 100, 5);
        }
        if (this.jobCheckOptions == null) {
            this.jobCheckOptions = new JobCheckOptions(this.generateQueueOptions.scheduler(), 1000, 2000, 10, false);
        }
        RequestHandler requestHandler = this.requestHandlerConstructor.construct(this.baseUrl, this.userAgent, this.apiKey, this.timeout, this.gson);
        RequestExecutors executors = new RequestExecutors(this.getExecutor, this.generateExecutor, this.generateQueueOptions, this.getQueueOptions, this.jobCheckOptions);
        MineSkinClientImpl client = new MineSkinClientImpl(requestHandler, executors);
        IQueueOptions iQueueOptions = executors.generateQueueOptions();
        if (iQueueOptions instanceof AutoGenerateQueueOptions) {
            AutoGenerateQueueOptions autoGenerate = (AutoGenerateQueueOptions)iQueueOptions;
            autoGenerate.setClient(client);
        }
        return client;
    }
}

