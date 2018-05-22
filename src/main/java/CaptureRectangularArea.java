import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.File;

import com.teamdev.jxcapture.Codec;
import com.teamdev.jxcapture.EncodingParameters;
import com.teamdev.jxcapture.VideoCapture;

/**
 * This example demonstrates how to capture a rectangular area of a screen.
 * <pre>
 * Platforms:           Windows
 * Image Source:        Desktop
 * Output video format: WMV or MP4
 * Output file:         Rectangle.wmv or Rectangle.mp4 depending on a platform
 *
 * @author Serge Piletsky
 */
public class CaptureRectangularArea implements Runnable{
	@Override
	public void run() {
		final VideoCapture videoCapture = VideoCapture.create();
        videoCapture.setCaptureArea(new Rectangle((1920-1024)/2, (1080-800)/2, 1024, 800));

        java.util.List<Codec> videoCodecs = videoCapture.getVideoCodecs();
        Codec videoCodec = videoCodecs.get(0);
        System.out.println("videoCodec = " + videoCodec);

        EncodingParameters encodingParameters = new EncodingParameters(new File("Rectangle." + videoCapture.getVideoFormat().getId()));
        encodingParameters.setSize(new Dimension(1024, 800));
        encodingParameters.setBitrate(500000);
        encodingParameters.setFramerate(30);
        encodingParameters.setCodec(videoCodec);
        System.out.println("encodingParameters = " + encodingParameters);

        videoCapture.setEncodingParameters(encodingParameters);
        videoCapture.start();

	}
}