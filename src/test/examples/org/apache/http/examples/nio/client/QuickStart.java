/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package test.examples.org.apache.http.examples.nio.client;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.client.methods.AsyncCharConsumer;
import org.apache.http.nio.client.methods.HttpAsyncMethods;
import org.apache.http.nio.protocol.HttpAsyncRequestProducer;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.nio.CharBuffer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;

public class QuickStart {

    public static void main(String[] args) throws Exception {
        CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault();
        HttpEntity entity = null;
        String jsonContent = "";
        try {
            // Start the client
            httpclient.start();

            // Execute request
            final HttpGet request1 = new HttpGet("http://viphp.sinaapp.com/baidu/translate/translate.php?origin=中文");
            Future<HttpResponse> future = httpclient.execute(request1, null);
            // and wait until response is received
            final HttpResponse response1 = future.get();
            System.out.println(request1.getRequestLine() + "->" + response1.getStatusLine());

            entity = response1.getEntity();
            jsonContent = EntityUtils.toString(entity, "UTF-8");
            System.out.println("jsonContent:" + jsonContent);
            // One most likely would want to use a callback for operation result
            final CountDownLatch latch1 = new CountDownLatch(1);
            final HttpGet request2 = new HttpGet("http://viphp.sinaapp.com/baidu/translate/translate.php?origin=中文");
            httpclient.execute(request2, new FutureCallback<HttpResponse>() {

                @Override
                public void completed(final HttpResponse response2) {
                    try {
                        latch1.countDown();
                        System.out.println(request2.getRequestLine() + "->" + response2.getStatusLine());
                        HttpEntity entity = response2.getEntity();
                        String jsonContent = EntityUtils.toString(entity, "UTF-8");
                        System.out.println("jsonContent2:" + jsonContent);
                    } catch (IOException ex) {
                        Logger.getLogger(QuickStart.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ParseException ex) {
                        Logger.getLogger(QuickStart.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                @Override
                public void failed(final Exception ex) {
                    latch1.countDown();
                    System.out.println(request2.getRequestLine() + "->" + ex);
                }

                @Override
                public void cancelled() {
                    latch1.countDown();
                    System.out.println(request2.getRequestLine() + " cancelled");
                }

            });
            latch1.await();

            // In real world one most likely would want also want to stream
            // request and response body content
            final CountDownLatch latch2 = new CountDownLatch(1);
            final HttpGet request3 = new HttpGet("http://viphp.sinaapp.com/baidu/translate/translate.php?origin=中文");
            HttpAsyncRequestProducer producer3 = HttpAsyncMethods.create(request3);
            AsyncCharConsumer<HttpResponse> consumer3 = new AsyncCharConsumer<HttpResponse>() {

                HttpResponse response;

                @Override
                protected void onResponseReceived(final HttpResponse response) {
                    this.response = response;
                }

                @Override
                protected void onCharReceived(final CharBuffer buf, final IOControl ioctrl) throws IOException {
                    // Do something useful
                    System.out.println(buf.toString());
                }

                @Override
                protected void releaseResources() {
                }

                @Override
                protected HttpResponse buildResult(final HttpContext context) {
                    return this.response;
                }

            };
            httpclient.execute(producer3, consumer3, new FutureCallback<HttpResponse>() {

                @Override
                public void completed(final HttpResponse response3) {
                    try {
                        latch2.countDown();
                        System.out.println(request2.getRequestLine() + "-3>" + response3.getStatusLine());
                        HttpEntity entity = response3.getEntity();
                        String jsonContent = EntityUtils.toString(entity, "UTF-8");
                        System.out.println("jsonContent3:" + jsonContent);
                    } catch (IOException ex) {
                        Logger.getLogger(QuickStart.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ParseException ex) {
                        Logger.getLogger(QuickStart.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                @Override
                public void failed(final Exception ex) {
                    latch2.countDown();
                    System.out.println(request2.getRequestLine() + "->" + ex);
                }

                @Override
                public void cancelled() {
                    latch2.countDown();
                    System.out.println(request2.getRequestLine() + " cancelled");
                }

            });
            latch2.await();

        } finally {
            httpclient.close();
        }
    }

}
