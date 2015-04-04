/*
 * Copyright (C) 2010-2014  Dmitry "PVOID" Petuhov
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.pvoid.apteryx.net;

import android.net.Uri;

import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.pvoid.apteryx.net.results.ResponseTag;
import org.pvoid.apteryx.net.results.Result;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

//@RunWith(RobolectricTestRunner.class)
//@Config(emulateSdk = 18)
public class RequestWorkTest {
//    @Test
//    public void requestHttpErrorCheck() throws Exception {
//        MockWebServer server = new MockWebServer();
//        server.enqueue(new MockResponse().setResponseCode(404));
//        server.play();
//
//        OsmpRequest request = Mockito.mock(OsmpRequest.class);
//        ResultFactories factories = Mockito.mock(ResultFactories.class);
//        ResultCallback handler = Mockito.mock(ResultCallback.class);
//        Mockito.when(request.getUri()).thenReturn(Uri.parse(server.getUrl("/").toString()));
//        RequestWork requestWork = new RequestWork(Mockito.mock(RequestScheduler.class), request,
//                factories, handler);
//        requestWork.run();
//        server.shutdown();
//        Mockito.verify(handler, Mockito.times(1)).onError();
//    }
//
//    @Test
//    public void cancelRequestCheck() throws Exception {
//        OsmpRequest request = Mockito.mock(OsmpRequest.class);
//        ResultFactories factories = Mockito.mock(ResultFactories.class);
//        ResultCallback handler = Mockito.mock(ResultCallback.class);
//        Mockito.when(request.getUri()).thenReturn(Uri.parse("http://localhost/"));
//        Mockito.when(handler.isCanceled()).thenReturn(true);
//        RequestWork requestWork = new RequestWork(Mockito.mock(RequestScheduler.class), request,
//                factories, handler);
//        requestWork.run();
//        Mockito.verify(handler, Mockito.never()).onError();
//        Mockito.verify(handler, Mockito.never()).onSuccess(Mockito.any(OsmpResponse.class));
//    }
//
//    @Test
//    public void nonXmlResponseCheck() throws Exception {
//        MockWebServer server = new MockWebServer();
//
//        MockResponse response = new MockResponse();
//        response.setBody("Plain text");
//
//        server.enqueue(response);
//        server.play();
//
//        OsmpRequest request = Mockito.mock(OsmpRequest.class);
//        ResultFactories factories = Mockito.mock(ResultFactories.class);
//        ResultCallback handler = Mockito.mock(ResultCallback.class);
//        Mockito.when(request.getUri()).thenReturn(Uri.parse(server.getUrl("/").toString()));
//        RequestWork requestWork = new RequestWork(Mockito.mock(RequestScheduler.class), request,
//                factories, handler);
//        requestWork.run();
//        server.shutdown();
//        Mockito.verify(handler, Mockito.times(1)).onError();
//    }
//
//    @Test
//    public void invalidXmlResponseCheck() throws Exception {
//        MockWebServer server = new MockWebServer();
//
//        MockResponse response = new MockResponse();
//        response.setBody("<?xml version=\"1.0\" encoding=\"windows-1251\"?><error/>");
//        server.enqueue(response);
//        server.play();
//
//        OsmpRequest request = Mockito.mock(OsmpRequest.class);
//        ResultFactories factories = Mockito.mock(ResultFactories.class);
//        ResultCallback handler = Mockito.mock(ResultCallback.class);
//        Mockito.when(request.getUri()).thenReturn(Uri.parse(server.getUrl("/").toString()));
//        RequestWork requestWork = new RequestWork(Mockito.mock(RequestScheduler.class), request,
//                factories, handler);
//        requestWork.run();
//        server.shutdown();
//        Mockito.verify(handler, Mockito.times(1)).onError();
//    }
//
//    @Test
//    public void requestCheck() throws Exception {
//        final String XML =
//            "<?xml version=\"1.0\" encoding=\"windows-1251\"?>\n" +
//            "<response result=\"0\">\n" +
//                "<terminals>\n" +
//                    "<getMessages/>\n" +
//                "</terminals>\n" +
//                "<agents>\n" +
//                    "<getBalance/>" +
//                "</agents>" +
//            "</response>";
//
//        MockWebServer server = new MockWebServer();
//        MockResponse response = new MockResponse();
//        response.setBody(XML);
//        server.enqueue(response);
//        server.play();
//
//        OsmpRequest request = Mockito.mock(OsmpRequest.class);
//        ResultFactories factories = Mockito.mock(ResultFactories.class);
//        ResultCallback handler = Mockito.mock(ResultCallback.class);
//        Mockito.when(request.getUri()).thenReturn(Uri.parse(server.getUrl("/").toString()));
//        RequestWork requestWork = new RequestWork(Mockito.mock(RequestScheduler.class), request,
//                factories, handler);
//        requestWork.run();
//        server.shutdown();
//
//        Mockito.verify(handler, Mockito.never()).onError();
//        Mockito.verify(handler, Mockito.times(1)).onSuccess(Mockito.any(OsmpResponse.class));
//    }
//
//    @Test
//    public void scheduleRequestCheck() throws Exception {
//        final String XML =
//                "<?xml version=\"1.0\" encoding=\"windows-1251\"?>\n" +
//                "<response result=\"0\">\n" +
//                    "<terminals>\n" +
//                        "<getMessages quid=\"20\" result=\"0\" status=\"1\" />\n" +
//                    "</terminals>\n" +
//                "</response>";
//
//        MockWebServer server = new MockWebServer();
//        MockResponse response = new MockResponse();
//        response.setBody(XML);
//        server.enqueue(response);
//        server.play();
//
//        OsmpRequest request = Mockito.mock(OsmpRequest.class);
//        OsmpRequest.Builder builder = Mockito.mock(OsmpRequest.Builder.class);
//        Mockito.when(request.buildUppon()).thenReturn(builder);
//        Mockito.when(builder.getInterface(Mockito.any(OsmpInterface.class)))
//                .thenReturn(Mockito.mock(OsmpRequest.CommandsList.class));
//        Mockito.when(builder.create()).thenReturn(request);
//
//        ResultFactories factories = Mockito.mock(ResultFactories.class);
//        Result result = Mockito.mock(Result.class);
//        ResultCallback handler = Mockito.mock(ResultCallback.class);
//        RequestScheduler scheduler = Mockito.mock(RequestScheduler.class);
//        Mockito.when(request.getUri()).thenReturn(Uri.parse(server.getUrl("/").toString()));
//
//        Mockito.when(result.isPending()).thenReturn(true);
//        Mockito.when(factories.build(Mockito.any(ResponseTag.class))).thenReturn(result);
//        RequestWork requestWork = new RequestWork(scheduler, request, factories, handler);
//        requestWork.run();
//
//        Mockito.verify(scheduler, Mockito.times(1)).schedule(Mockito.any(RequestWork.class));
//    }
}
