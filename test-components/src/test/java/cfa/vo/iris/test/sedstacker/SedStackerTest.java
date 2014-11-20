package cfa.vo.iris.test.sedstacker;

import cfa.vo.interop.SAMPFactory;
import cfa.vo.interop.SAMPMessage;
import cfa.vo.iris.interop.SedSAMPController;
import cfa.vo.iris.test.sedstacker.samp.SedStackerNormalizePayload;
import cfa.vo.iris.test.sedstacker.samp.SegmentPayload;
import cfa.vo.sherpa.SherpaClient;
import junit.framework.Assert;
import org.astrogrid.samp.Response;

/**
 * Created by olaurino on 11/20/14.
 */
public class SedStackerTest {

    public static void main(String[] args) throws Exception {

//        x1 = encode_string(numpy.array([1,5,10,15,50,100]))
//        y1 = encode_string(numpy.array([1,5,10,15,50,100]) * 0.1)
//        yerr1 = encode_string(numpy.array([1,5,10,15,50,100]) * 0.01)

        SedSAMPController controller = new SedSAMPController("SEDStacker", "SEDStacker", null);
        controller.setAutoRunHub(false);
        controller.start(false);

        try {

            Thread.sleep(2000);
            System.out.println();

            while (!controller.isConnected()) {
                System.out.println("waiting connection");
                Thread.sleep(1000);
            }

            SedStackerNormalizePayload payload = (SedStackerNormalizePayload) SAMPFactory.get(SedStackerNormalizePayload.class);

            double[] x1 = new double[]{1, 5, 10, 15, 50, 100};

            double[] y1 = new double[x1.length];
            for (int i = 0; i < x1.length; i++) {
                y1[i] = 0.1 * x1[i];
            }

            double[] yerr1 = new double[x1.length];
            for (int i = 0; i < x1.length; i++) {
                yerr1[i] = 0.01 * x1[i];
            }

            double[] x2 = new double[]{2, 4, 5, 8, 10};

            double[] y2 = new double[]{1, 2, 3, 4, 5};

            double[] yerr2 = new double[]{0.1, 0.2, 0.3, 0.4, 0.5};

            double[] x3 = new double[]{0.5, 1.5, 3.0, 5.0, 10.5, 21.0};

            double[] y3 = new double[]{5.0, 15.0, 7.0, 4.5, 13.5, 10.5};

            double[] yerr3 = new double[]{0.5, 1.5, 0.7, 0.45, 1.35, 1.05};

//
//        x2 = encode_string(numpy.array([2,4,5,8,10]))
//        y2 = encode_string(numpy.arange(5)+1.0)
//        yerr2 = encode_string(numpy.arange(5)+1.0*0.1)
//
//        y3 = numpy.array([5.0, 15.0, 7.0, 4.5, 13.5, 10.5])
//        yerr3 = encode_string(y3*0.1)
//        y3 = encode_string(y3)
//        x3 = encode_string(numpy.array([0.5, 1.5, 3.0, 5.0, 10.5, 21.0]))

            SegmentPayload segment1 = (SegmentPayload) SAMPFactory.get(SegmentPayload.class);
            segment1.setX(x1);
            segment1.setY(y1);
            segment1.setYerr(yerr1);
            segment1.setRedshift(0.1);

            SegmentPayload segment2 = (SegmentPayload) SAMPFactory.get(SegmentPayload.class);
            segment2.setX(x2);
            segment2.setY(y2);
            segment2.setYerr(yerr2);
            segment2.setRedshift(0.2);

            SegmentPayload segment3 = (SegmentPayload) SAMPFactory.get(SegmentPayload.class);
            segment3.setX(x3);
            segment3.setY(y3);
            segment3.setYerr(yerr3);
            segment3.setRedshift(0.3);

            payload.addSegment(segment1);
            payload.addSegment(segment2);
            payload.addSegment(segment3);
//
//
//        params = {}
//
//        segment1 = {'x' : x1, 'y' : y1, 'yerr' : yerr1}
//        segment2 = {'x' : x2, 'y' : y2, 'yerr' : yerr2}
//        segment3 = {'x' : x3, 'y' : y3, 'yerr' : yerr3}
//
//        params['segments']= [segment1, segment2, segment3]
//

//        params['norm_operator'] = 0;
//        params['y0'] = 1.0
//        params['xmin'] = 'min'
//        params['xmax'] = 'max'
//        params['stats'] = 'avg'
//        params['integrate'] = 'true'

            payload.setNormOperator(0);
            payload.setY0(1.0);
            payload.setXmin("min");
            payload.setXmax("max");
            payload.setStats("avg");
            payload.setIntegrate(Boolean.TRUE);

            SAMPMessage message = SAMPFactory.createMessage("stack.normalize", payload, SedStackerNormalizePayload.class);

            SherpaClient client = new SherpaClient(controller);

            Response rspns = controller.callAndWait(client.findSherpa(), message.get(), 10);
            if (client.isException(rspns)) {
                Exception ex = client.getException(rspns);
                throw ex;
            }

            SedStackerNormalizePayload response = (SedStackerNormalizePayload) SAMPFactory.get(rspns.getResult(), SedStackerNormalizePayload.class);


//
//        response = self.cli.callAndWait(
//                mtypes.cli.getPublicId(),
//                {'samp.mtype'  : MTYPE_STACK_NORMALIZE,
//                'samp.params' : params},
//        "10")
//
//        assert response['samp.status'] == 'samp.ok'
//
//        results = response['samp.result']
//
//        norm_stack = DictionaryClass(results)
//
//        numpy.testing.assert_array_almost_equal(decode_string(norm_stack.segments[0].y), 0.49234923*decode_string(y1))
//        numpy.testing.assert_array_almost_equal(decode_string(norm_stack.segments[1].y), 9.846*decode_string(y2))
//        self.assertAlmostEqual(float(norm_stack.segments[2].norm_constant), 1.1529274)

            double[] resy1 = response.getSegments().get(0).getY();

            for (int i = 0; i < resy1.length; i++) {
                Assert.assertEquals(0.49234923 * y1[i], resy1[i], 0.00001);
            }

        } finally {
            controller.stop();
        }

    }
}
