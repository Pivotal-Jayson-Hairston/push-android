package com.pivotal.cf.mobile.analyticssdk.jobs;

import android.net.Uri;
import android.test.MoreAsserts;

import com.pivotal.cf.mobile.analyticssdk.backend.FakeBackEndSendEventsApiRequest;
import com.pivotal.cf.mobile.analyticssdk.model.events.Event;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.List;

public class SendEventsJobTest extends JobTest {

    public void testWithEmptyDatabase() throws InterruptedException {

        final SendEventsJob job = new SendEventsJob();
        job.run(getJobParams(new JobResultListener() {

            @Override
            public void onJobComplete(int resultCode) {
                assertEquals(SendEventsJob.RESULT_NO_WORK_TO_DO, resultCode);
                semaphore.release();
            }
        }));

        semaphore.acquire();
        Assert.assertEquals(0, eventsStorage.getNumberOfEvents());
        assertFalse(backEndMessageReceiptApiRequest.wasRequestAttempted());
    }

    public void testSuccessfulSend() throws InterruptedException {

        final Uri uri1 = saveEventWithStatus(Event.Status.NOT_POSTED);
        final Uri uri2 = saveEventWithStatus(Event.Status.POSTING);
        final Uri uri3 = saveEventWithStatus(Event.Status.POSTING_ERROR);
        final Uri uri4 = saveEventWithStatus(Event.Status.POSTED);

        backEndMessageReceiptApiRequest.setWillBeSuccessfulRequest(true);
        backEndMessageReceiptApiRequest.setRequestHook(new FakeBackEndSendEventsApiRequest.RequestHook() {

            @Override
            public void onRequestMade(FakeBackEndSendEventsApiRequest request, List<Uri> uris) {
                assertEquals(2, uris.size());
                final List<Uri> expectedUrisInRequest = new ArrayList<Uri>(2);
                expectedUrisInRequest.add(uri1);
                expectedUrisInRequest.add(uri3);
                MoreAsserts.assertContentsInAnyOrder(expectedUrisInRequest, uris);

                // Checks that events have the POSTING status while they are being posted
                assertEventHasStatus(uri1, Event.Status.POSTING);
                assertEventHasStatus(uri1, Event.Status.POSTING);
                assertEventHasStatus(uri1, Event.Status.POSTING);
                assertEventHasStatus(uri1, Event.Status.POSTED);
            }
        });

        Assert.assertEquals(4, eventsStorage.getNumberOfEvents());

        final SendEventsJob job = new SendEventsJob();
        job.run(getJobParams(new JobResultListener() {

            @Override
            public void onJobComplete(int resultCode) {
                assertEquals(JobResultListener.RESULT_SUCCESS, resultCode);
                semaphore.release();
            }
        }));

        semaphore.acquire();
        Assert.assertEquals(2, eventsStorage.getNumberOfEvents());
        assertTrue(backEndMessageReceiptApiRequest.wasRequestAttempted());
        Assert.assertEquals(2, backEndMessageReceiptApiRequest.numberOfEventsSent());

        assertEventHasStatus(uri2, Event.Status.POSTING);
        assertEventHasStatus(uri4, Event.Status.POSTED);
        assertEventNotInStorage(uri1); // posted events should be removed
        assertEventNotInStorage(uri3);
    }

    public void testFailedSendWithOneNotPostedEventInStorage() throws InterruptedException {

        final Uri uri = saveEventWithStatus(Event.Status.NOT_POSTED);

        backEndMessageReceiptApiRequest.setWillBeSuccessfulRequest(false);
        backEndMessageReceiptApiRequest.setRequestHook(new FakeBackEndSendEventsApiRequest.RequestHook() {

            @Override
            public void onRequestMade(FakeBackEndSendEventsApiRequest request, List<Uri> uris) {
                assertEquals(1, uris.size());
                assertEquals(uri, uris.get(0));
                assertEventHasStatus(uri, Event.Status.POSTING);
            }
        });

        final SendEventsJob job = new SendEventsJob();
        job.run(getJobParams(new JobResultListener() {

            @Override
            public void onJobComplete(int resultCode) {
                assertEquals(SendEventsJob.RESULT_FAILED_TO_SEND_RECEIPTS, resultCode);
                semaphore.release();
            }
        }));

        semaphore.acquire();
        Assert.assertEquals(1, eventsStorage.getNumberOfEvents());
        assertTrue(backEndMessageReceiptApiRequest.wasRequestAttempted());
        Assert.assertEquals(0, backEndMessageReceiptApiRequest.numberOfEventsSent());
        assertEventHasStatus(uri, Event.Status.POSTING_ERROR);
    }

    public void testEquals() {
        final SendEventsJob job1 = new SendEventsJob();
        final SendEventsJob job2 = new SendEventsJob();
        assertEquals(event1, event1);
        assertEquals(job1, job2);
    }

    public void testParcelsData() {
        final SendEventsJob inputJob = new SendEventsJob();
        final SendEventsJob outputJob = getJobViaParcel(inputJob);
        assertNotNull(outputJob);
        assertEquals(inputJob, outputJob);
    }
}