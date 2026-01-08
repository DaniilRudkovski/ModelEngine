/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.gson.JsonObject
 */
package org.mineskin;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mineskin.GenerateClient;
import org.mineskin.JobChecker;
import org.mineskin.MineSkinClient;
import org.mineskin.MiscClient;
import org.mineskin.QueueClient;
import org.mineskin.RequestExecutors;
import org.mineskin.RequestQueue;
import org.mineskin.SkinsClient;
import org.mineskin.data.JobInfo;
import org.mineskin.data.JobReference;
import org.mineskin.data.NullJobReference;
import org.mineskin.data.RateLimitInfo;
import org.mineskin.data.SkinInfo;
import org.mineskin.data.UserInfo;
import org.mineskin.exception.MineSkinRequestException;
import org.mineskin.exception.MineskinException;
import org.mineskin.options.IJobCheckOptions;
import org.mineskin.request.GenerateRequest;
import org.mineskin.request.RequestHandler;
import org.mineskin.request.UploadRequestBuilder;
import org.mineskin.request.UrlRequestBuilder;
import org.mineskin.request.UserRequestBuilder;
import org.mineskin.request.source.UploadSource;
import org.mineskin.response.GenerateResponse;
import org.mineskin.response.GenerateResponseImpl;
import org.mineskin.response.JobResponse;
import org.mineskin.response.JobResponseImpl;
import org.mineskin.response.MineSkinResponse;
import org.mineskin.response.QueueResponse;
import org.mineskin.response.QueueResponseImpl;
import org.mineskin.response.SkinResponse;
import org.mineskin.response.SkinResponseImpl;
import org.mineskin.response.UserResponse;
import org.mineskin.response.UserResponseImpl;

public class MineSkinClientImpl
implements MineSkinClient {
    public static final Logger LOGGER = Logger.getLogger(MineSkinClient.class.getName());
    private final RequestExecutors executors;
    private final RequestHandler requestHandler;
    private final RequestQueue generateQueue;
    private final RequestQueue getQueue;
    private final QueueClient queueClient = new QueueClientImpl();
    private final GenerateClient generateClient = new GenerateClientImpl();
    private final SkinsClient skinsClient = new SkinsClientImpl();
    private final MiscClient miscClient = new MiscClientImpl();

    public MineSkinClientImpl(RequestHandler requestHandler, RequestExecutors executors) {
        this.requestHandler = (RequestHandler)Preconditions.checkNotNull((Object)requestHandler);
        this.executors = (RequestExecutors)Preconditions.checkNotNull((Object)executors);
        this.generateQueue = new RequestQueue(executors.generateQueueOptions());
        this.getQueue = new RequestQueue(executors.getQueueOptions());
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    @Override
    public QueueClient queue() {
        return this.queueClient;
    }

    @Override
    public GenerateClient generate() {
        return this.generateClient;
    }

    @Override
    public SkinsClient skins() {
        return this.skinsClient;
    }

    @Override
    public MiscClient misc() {
        return this.miscClient;
    }

    class QueueClientImpl
    implements QueueClient {
        QueueClientImpl() {
        }

        @Override
        public CompletableFuture<QueueResponse> submit(GenerateRequest request) {
            if (request instanceof UploadRequestBuilder) {
                UploadRequestBuilder uploadRequestBuilder = (UploadRequestBuilder)request;
                return this.queueUpload(uploadRequestBuilder);
            }
            if (request instanceof UrlRequestBuilder) {
                UrlRequestBuilder urlRequestBuilder = (UrlRequestBuilder)request;
                return this.queueUrl(urlRequestBuilder);
            }
            if (request instanceof UserRequestBuilder) {
                UserRequestBuilder userRequestBuilder = (UserRequestBuilder)request;
                return this.queueUser(userRequestBuilder);
            }
            throw new MineskinException("Unknown request builder type: " + String.valueOf(request.getClass()));
        }

        CompletableFuture<QueueResponse> queueUpload(UploadRequestBuilder builder) {
            LOGGER.log(Level.FINER, "Adding upload request to internal queue: {0}", builder);
            return MineSkinClientImpl.this.generateQueue.submit(() -> {
                QueueResponseImpl queueResponseImpl;
                block9: {
                    Map<String, String> data = builder.options().toMap();
                    UploadSource source = builder.getUploadSource();
                    Preconditions.checkNotNull((Object)source);
                    InputStream inputStream = source.getInputStream();
                    try {
                        LOGGER.log(Level.FINER, "Submitting to MineSkin queue: {0}", builder);
                        QueueResponseImpl res = MineSkinClientImpl.this.requestHandler.postFormDataFile("/v2/queue", "file", "mineskinjava", inputStream, data, JobInfo.class, QueueResponseImpl::new);
                        this.handleGenerateResponse(res);
                        queueResponseImpl = res;
                        if (inputStream == null) break block9;
                    }
                    catch (Throwable throwable) {
                        try {
                            if (inputStream != null) {
                                try {
                                    inputStream.close();
                                }
                                catch (Throwable throwable2) {
                                    throwable.addSuppressed(throwable2);
                                }
                            }
                            throw throwable;
                        }
                        catch (IOException e) {
                            throw new MineskinException(e);
                        }
                        catch (MineSkinRequestException e) {
                            this.handleGenerateResponse(e.getResponse());
                            throw e;
                        }
                    }
                    inputStream.close();
                }
                return queueResponseImpl;
            }, MineSkinClientImpl.this.executors.generateExecutor());
        }

        CompletableFuture<QueueResponse> queueUrl(UrlRequestBuilder builder) {
            LOGGER.log(Level.FINER, "Adding url request to internal queue: {0}", builder);
            return MineSkinClientImpl.this.generateQueue.submit(() -> {
                try {
                    JsonObject body = builder.options().toJson();
                    URL url = builder.getUrl();
                    Preconditions.checkNotNull((Object)url);
                    body.addProperty("url", url.toString());
                    LOGGER.log(Level.FINER, "Submitting to MineSkin queue: {0}", builder);
                    QueueResponseImpl res = MineSkinClientImpl.this.requestHandler.postJson("/v2/queue", body, JobInfo.class, QueueResponseImpl::new);
                    this.handleGenerateResponse(res);
                    return res;
                }
                catch (IOException e) {
                    throw new MineskinException(e);
                }
                catch (MineSkinRequestException e) {
                    this.handleGenerateResponse(e.getResponse());
                    throw e;
                }
            }, MineSkinClientImpl.this.executors.generateExecutor());
        }

        CompletableFuture<QueueResponse> queueUser(UserRequestBuilder builder) {
            LOGGER.log(Level.FINER, "Adding user request to internal queue: {0}", builder);
            return MineSkinClientImpl.this.generateQueue.submit(() -> {
                try {
                    JsonObject body = builder.options().toJson();
                    UUID uuid = builder.getUuid();
                    Preconditions.checkNotNull((Object)uuid);
                    body.addProperty("user", uuid.toString());
                    LOGGER.log(Level.FINER, "Submitting to MineSkin queue: {0}", builder);
                    QueueResponseImpl res = MineSkinClientImpl.this.requestHandler.postJson("/v2/queue", body, JobInfo.class, QueueResponseImpl::new);
                    this.handleGenerateResponse(res);
                    return res;
                }
                catch (IOException e) {
                    throw new MineskinException(e);
                }
                catch (MineSkinRequestException e) {
                    this.handleGenerateResponse(e.getResponse());
                    throw e;
                }
            }, MineSkinClientImpl.this.executors.generateExecutor());
        }

        private void handleGenerateResponse(MineSkinResponse<?> response0) {
            if (!(response0 instanceof QueueResponse)) {
                return;
            }
            QueueResponse response = (QueueResponse)response0;
            RateLimitInfo rateLimit = response.getRateLimit();
            if (rateLimit == null) {
                return;
            }
            long nextRelative = rateLimit.next().relative();
            if (nextRelative > 0L) {
                MineSkinClientImpl.this.generateQueue.setNextRequest(Math.max(MineSkinClientImpl.this.generateQueue.getNextRequest(), System.currentTimeMillis() + nextRelative));
            }
        }

        @Override
        public CompletableFuture<JobResponse> get(JobInfo jobInfo) {
            Preconditions.checkNotNull((Object)jobInfo);
            return this.get(jobInfo.id());
        }

        @Override
        public CompletableFuture<JobResponse> get(String id) {
            Preconditions.checkNotNull((Object)id);
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return MineSkinClientImpl.this.requestHandler.getJson("/v2/queue/" + id, JobInfo.class, JobResponseImpl::new);
                }
                catch (IOException e) {
                    throw new MineskinException(e);
                }
            }, MineSkinClientImpl.this.executors.getExecutor());
        }

        @Override
        public CompletableFuture<JobReference> waitForCompletion(JobInfo jobInfo) {
            Preconditions.checkNotNull((Object)jobInfo);
            if (jobInfo.id() == null) {
                return CompletableFuture.completedFuture(new NullJobReference(jobInfo));
            }
            IJobCheckOptions options = MineSkinClientImpl.this.executors.jobCheckOptions();
            return new JobChecker(MineSkinClientImpl.this, jobInfo, options).check();
        }
    }

    class GenerateClientImpl
    implements GenerateClient {
        GenerateClientImpl() {
        }

        @Override
        public CompletableFuture<GenerateResponse> submitAndWait(GenerateRequest request) {
            if (request instanceof UploadRequestBuilder) {
                UploadRequestBuilder uploadRequestBuilder = (UploadRequestBuilder)request;
                return this.generateUpload(uploadRequestBuilder);
            }
            if (request instanceof UrlRequestBuilder) {
                UrlRequestBuilder urlRequestBuilder = (UrlRequestBuilder)request;
                return this.generateUrl(urlRequestBuilder);
            }
            if (request instanceof UserRequestBuilder) {
                UserRequestBuilder userRequestBuilder = (UserRequestBuilder)request;
                return this.generateUser(userRequestBuilder);
            }
            throw new MineskinException("Unknown request builder type: " + String.valueOf(request.getClass()));
        }

        CompletableFuture<GenerateResponse> generateUpload(UploadRequestBuilder builder) {
            LOGGER.log(Level.FINER, "Adding upload request to internal generate queue: {0}", builder);
            return MineSkinClientImpl.this.generateQueue.submit(() -> {
                GenerateResponseImpl generateResponseImpl;
                block9: {
                    Map<String, String> data = builder.options().toMap();
                    UploadSource source = builder.getUploadSource();
                    Preconditions.checkNotNull((Object)source);
                    InputStream inputStream = source.getInputStream();
                    try {
                        LOGGER.log(Level.FINER, "Submitting to MineSkin generate: {0}", builder);
                        GenerateResponseImpl res = MineSkinClientImpl.this.requestHandler.postFormDataFile("/v2/generate", "file", "mineskinjava", inputStream, data, SkinInfo.class, GenerateResponseImpl::new);
                        this.handleGenerateResponse(res);
                        generateResponseImpl = res;
                        if (inputStream == null) break block9;
                    }
                    catch (Throwable throwable) {
                        try {
                            if (inputStream != null) {
                                try {
                                    inputStream.close();
                                }
                                catch (Throwable throwable2) {
                                    throwable.addSuppressed(throwable2);
                                }
                            }
                            throw throwable;
                        }
                        catch (IOException e) {
                            throw new MineskinException(e);
                        }
                        catch (MineSkinRequestException e) {
                            this.handleGenerateResponse(e.getResponse());
                            throw e;
                        }
                    }
                    inputStream.close();
                }
                return generateResponseImpl;
            }, MineSkinClientImpl.this.executors.generateExecutor());
        }

        CompletableFuture<GenerateResponse> generateUrl(UrlRequestBuilder builder) {
            LOGGER.log(Level.FINER, "Adding url request to internal generate queue: {0}", builder);
            return MineSkinClientImpl.this.generateQueue.submit(() -> {
                try {
                    JsonObject body = builder.options().toJson();
                    URL url = builder.getUrl();
                    Preconditions.checkNotNull((Object)url);
                    body.addProperty("url", url.toString());
                    LOGGER.log(Level.FINER, "Submitting to MineSkin generate: {0}", builder);
                    GenerateResponseImpl res = MineSkinClientImpl.this.requestHandler.postJson("/v2/generate", body, SkinInfo.class, GenerateResponseImpl::new);
                    this.handleGenerateResponse(res);
                    return res;
                }
                catch (IOException e) {
                    throw new MineskinException(e);
                }
                catch (MineSkinRequestException e) {
                    this.handleGenerateResponse(e.getResponse());
                    throw e;
                }
            }, MineSkinClientImpl.this.executors.generateExecutor());
        }

        CompletableFuture<GenerateResponse> generateUser(UserRequestBuilder builder) {
            LOGGER.log(Level.FINER, "Adding user request to internal generate queue: {0}", builder);
            return MineSkinClientImpl.this.generateQueue.submit(() -> {
                try {
                    JsonObject body = builder.options().toJson();
                    UUID uuid = builder.getUuid();
                    Preconditions.checkNotNull((Object)uuid);
                    body.addProperty("user", uuid.toString());
                    LOGGER.log(Level.FINER, "Submitting to MineSkin generate: {0}", builder);
                    GenerateResponseImpl res = MineSkinClientImpl.this.requestHandler.postJson("/v2/generate", body, SkinInfo.class, GenerateResponseImpl::new);
                    this.handleGenerateResponse(res);
                    return res;
                }
                catch (IOException e) {
                    throw new MineskinException(e);
                }
                catch (MineSkinRequestException e) {
                    this.handleGenerateResponse(e.getResponse());
                    throw e;
                }
            }, MineSkinClientImpl.this.executors.generateExecutor());
        }

        private void handleGenerateResponse(MineSkinResponse<?> response0) {
            LOGGER.log(Level.FINER, "Handling generate response: {0}", response0);
            if (!(response0 instanceof GenerateResponse)) {
                return;
            }
            GenerateResponse response = (GenerateResponse)response0;
            RateLimitInfo rateLimit = response.getRateLimit();
            if (rateLimit == null) {
                return;
            }
            long nextRelative = rateLimit.next().relative();
            if (nextRelative > 0L) {
                MineSkinClientImpl.this.generateQueue.setNextRequest(Math.max(MineSkinClientImpl.this.generateQueue.getNextRequest(), System.currentTimeMillis() + nextRelative));
            }
        }
    }

    class SkinsClientImpl
    implements SkinsClient {
        SkinsClientImpl() {
        }

        @Override
        public CompletableFuture<SkinResponse> get(UUID uuid) {
            Preconditions.checkNotNull((Object)uuid);
            return this.get(uuid.toString());
        }

        @Override
        public CompletableFuture<SkinResponse> get(String uuid) {
            Preconditions.checkNotNull((Object)uuid);
            return MineSkinClientImpl.this.getQueue.submit(() -> {
                try {
                    return MineSkinClientImpl.this.requestHandler.getJson("/v2/skins/" + uuid, SkinInfo.class, SkinResponseImpl::new);
                }
                catch (IOException e) {
                    throw new MineskinException(e);
                }
            }, MineSkinClientImpl.this.executors.getExecutor());
        }
    }

    class MiscClientImpl
    implements MiscClient {
        MiscClientImpl() {
        }

        @Override
        public CompletableFuture<UserResponse> getUser() {
            return MineSkinClientImpl.this.getQueue.submit(() -> {
                try {
                    return MineSkinClientImpl.this.requestHandler.getJson("/v2/me", UserInfo.class, UserResponseImpl::new);
                }
                catch (IOException e) {
                    throw new MineskinException(e);
                }
            }, MineSkinClientImpl.this.executors.getExecutor());
        }
    }
}

