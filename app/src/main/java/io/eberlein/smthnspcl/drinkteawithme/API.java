package io.eberlein.smthnspcl.drinkteawithme;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import java.security.cert.Certificate;
import java.security.cert.CertificateException;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

class API {
    private TeaService service;
    //private static String baseURL = "http://192.168.2.117:7344/";
    private static String baseURL = "https://teawth.me/";

    API(Context context) {
        service = createService(context);
    }

    private static OkHttpClient buildClient(Context context) {
        OkHttpClient client = null;

        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream cert = context.getResources().openRawResource(R.raw.cert);

            Certificate ca = cf.generateCertificate(cert);
            cert.close();

            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);

            client = new OkHttpClient.Builder().sslSocketFactory(sslContext.getSocketFactory()).build();

        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException | KeyManagementException e) {
            e.printStackTrace();
        }

        return client;
    }

    private static TeaService createService(Context context) {
        Retrofit client = new Retrofit.Builder().baseUrl(API.baseURL).client(buildClient(context)).addConverterFactory(GsonConverterFactory.create()).build();
        return client.create(TeaService.class);
    }

    private void doEnqueuedCall_Success(Call<SuccessResponse> call, onSomething onSuccess, onSomething onFailure, onSomething onError) {
        call.enqueue(new Callback<SuccessResponse>() {
            @Override
            public void onResponse(Call<SuccessResponse> call, Response<SuccessResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().success) onSuccess.execute();
                    else onFailure.execute();
                } else {
                    onFailure.execute();
                }
            }

            @Override
            public void onFailure(Call<SuccessResponse> call, Throwable t) {
                t.printStackTrace();
                onError.execute(); // todo own onSomething class
            }
        });
    }

    private void doEnqueuedCall_String(Call<StringResponse> call, onSomething onSuccess, onSomething onFailure, onSomething onError) {
        call.enqueue(new Callback<StringResponse>() {
            @Override
            public void onResponse(Call<StringResponse> call, Response<StringResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().value != null) onSuccess.execute(response.body().value);
                    else onFailure.execute();
                } else {
                    onFailure.execute();
                }
            }

            @Override
            public void onFailure(Call<StringResponse> call, Throwable t) {
                t.printStackTrace();
                onError.execute(); // todo own onSomething class
            }
        });
    }

    void createUser(User data, onSomething onSuccess, onSomething onFailure, onSomething onError) {
        this.doEnqueuedCall_Success(service.createUser(data), onSuccess, onFailure, onError);
    }

    void loginUser(User data, onSomething onSuccess, onSomething onFailure, onSomething onError) {
        this.doEnqueuedCall_Success(service.loginUser(data), onSuccess, onFailure, onError);
    }

    void inviteUser(User data, onSomething onSuccess, onSomething onFailure, onSomething onError) {
        this.doEnqueuedCall_String(service.inviteUser(data), onSuccess, onFailure, onError);
    }

    public interface TeaService {
        @POST("api/user/create")
        Call<SuccessResponse> createUser(@Body User data);

        @POST("api/user/login")
        Call<SuccessResponse> loginUser(@Body User data);

        @POST("api/user/invite")
        Call<StringResponse> inviteUser(@Body User data);
    }

    public interface onSomething {
        void execute();

        void execute(String data);
    }

    public static class on implements onSomething {
        @Override
        public void execute() {

        }

        @Override
        public void execute(String data) {

        }
    }

    class IResponse {
        String key;
    }

    class SuccessResponse extends IResponse {
        Boolean success;
    }

    class StringResponse extends IResponse {
        String value;
    }
}
