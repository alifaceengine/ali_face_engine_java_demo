import com.alibaba.cloud.faceengine.*;
import com.alibaba.cloud.faceengine.Error;
import com.alibaba.cloud.faceengine.Image;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import com.github.sarxos.webcam.ds.vlcj.VlcjDriver;
import uk.co.caprica.vlcj.medialist.MediaListItem;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FaceRecognize_RTSP_NoUI_Demo implements Runnable, FaceRecognize.RecognizeVideoListener {
    private static final long serialVersionUID = 1L;
    private static final Executor EXECUTOR = Executors.newCachedThreadPool();
    private Webcam webcam = null;
    private WebcamPanel.Painter painter = null;
    private Face[] faces = null;

    private static final String BASE_PERSONS[] = {
            "liudehua_feature1.jpg", "liudehua_feature2.jpg",
            "zhangxueyou_feature1.jpg", "zhangxueyou_feature2.jpg"};

    private static final String VENDOR_KEY = "eyJ2ZW5kb3JJZCI6ImNlc2hpIiwicm9sZSI6MiwiY29kZSI6Ijc1QTdBREMyNDNENzY5QjRDNDU2M0JDMUVFRkI0QTJFIiwiZXhwaXJlIjoiMjAxOTAzMzEiLCJ0eXBlIjoxfQ==";
    private static String PICTURE_ROOT = System.getProperty("user.dir") + "/pictures/";
    private static int RunMode = Mode.TERMINAL;
    //private static int RunMode = Mode.CLOUD;
    private static String GROUP_NAME = "STAFF";
    private static Group sGroup = new Group();
    private RecognizeResult[] mRecognizeResult;
    private DetectParameter mDetectParameter;


    public FaceRecognize_RTSP_NoUI_Demo() {
        super();
        String name = "FaceRecognize_RTSP_NoUI_Demo";
        String rtsp = "rtsp://admin:xiolift123@192.168.1.220/";
        Webcam.setDriver(new VlcjDriver(Arrays.asList(new MediaListItem(name, rtsp, new ArrayList<MediaListItem>()))));
        webcam = Webcam.getWebcams().get(0);
        webcam.getLock().disable();
        webcam.setViewSize(WebcamResolution.VGA.getSize());
        webcam.open();

        EXECUTOR.execute(this);
    }

    @Override
    public void run() {
        //initialize
        FaceEngine.enableDebug(true);
        int error = FaceEngine.authorize(VENDOR_KEY);
        if (error != Error.OK) {
            return;
        }

        FaceEngine.setCloudLoginAccount("admin", "admin");
        FaceEngine.setPersistencePath("./");

        FaceRegister faceRegister = FaceRegister.createInstance();
        if (faceRegister == null) {
            return;
        }

        FaceRecognize faceRecognize = FaceRecognize.createInstance(GROUP_NAME, RunMode);
        if (faceRecognize == null) {
            return;
        }

        FaceDetect faceDetect = FaceDetect.createInstance(RunMode);
        if (faceDetect == null) {
            return;
        }

        mDetectParameter = faceDetect.getVideoParameter();
        mDetectParameter.checkQuality = 0;
        mDetectParameter.checkLiveness = 0;
        mDetectParameter.checkAge = 0;
        mDetectParameter.checkGender = 0;
        mDetectParameter.checkExpression = 0;
        mDetectParameter.checkGlass = 0;
        faceDetect.setVideoParameter(mDetectParameter);


        //create group
        if (true) {
            sGroup.name = GROUP_NAME;
            sGroup.modelType = ModelType.MODEL_SMALL;
            error = faceRegister.createGroup(sGroup);
            if (error != Error.OK && error != Error.ERROR_EXISTED && error != Error.ERROR_CLOUD_EXISTED_ERROR) {
                throw new RuntimeException("createGroup " + GROUP_NAME + " error:" + error);
            } else {
                printf("createGroup OK:" + error + " groupId:" + sGroup.id);
            }
        }

        //register pictures
        if (true) {
            registerPictures(faceDetect, faceRegister);
        }


        //set the group to recognize
        {
            faceRecognize.setRecognizeVideoListener(this);
        }


        //recognize
        while (true) {
            printf("loop begin");
            if (!webcam.isOpen()) {
                break;
            }

            BufferedImage originalImage = webcam.getImage();
            printf("get a image : " + originalImage.getWidth() + "x" + originalImage.getHeight());
            int pixels[] = originalImage.getRGB(0, 0, originalImage.getWidth(), originalImage.getHeight(), null, 0, originalImage.getWidth());
            byte[] bgr = new byte[originalImage.getWidth() * originalImage.getHeight() * 3];

            int index = 0;
            for (int i = 0; i < pixels.length; i++) {
                int p = pixels[i];
                bgr[index++] = (byte) (p & 0xff);
                bgr[index++] = (byte) ((p >> 8) & 0xff);
                bgr[index++] = (byte) ((p >> 16) & 0xff);
            }

            Image image = new Image();
            image.data = bgr;
            image.width = originalImage.getWidth();
            image.height = originalImage.getHeight();

            printf("detectVideo begin");
            faces = faceDetect.detectVideo(image);
            printf("detectVideo end");

            if (faces == null) {
                continue;
            }

            for (int i = 0; i < faces.length; i++) {
                printf("faces[" + i + "] : " + faces[i]);
            }

            faceRecognize.recognizeVideo(image, faces);
            printf("recognizeVideo end");
        }

        FaceDetect.deleteInstance(faceDetect);
        FaceRecognize.deleteInstance(faceRecognize);
        FaceRegister.deleteInstance(faceRegister);
    }

    private void registerPictures(FaceDetect faceDetect, FaceRegister faceRegister) {
        for (int i = 0; i < BASE_PERSONS.length; i++) {
            String personName = BASE_PERSONS[i].split("_")[0];
            String featureName = BASE_PERSONS[i].split("_")[1].split("\\.")[0];

            byte[] imageData = Utils.loadFile(Utils.PICTURE_ROOT + BASE_PERSONS[i]);
            if (imageData == null) {
                throw new RuntimeException("loadFile " + BASE_PERSONS[i] + " error");
            }

            Image image = new Image();
            image.data = imageData;
            image.format = ImageFormat.ImageFormat_UNKNOWN;
            Face faces[] = faceDetect.detectPicture(image);
            if (faces == null) {
                throw new RuntimeException("detectPicture " + BASE_PERSONS[i] + " error");
            }


            String featureStr = faceRegister.extractFeature(image, faces[0], ModelType.MODEL_SMALL);
            if (featureStr == null) {
                throw new RuntimeException("extractFeature " + BASE_PERSONS[i] + " error");
            }


            Person person = new Person();
            person.name = personName;
            int error = faceRegister.addPerson(sGroup.id, person);
            if (error != Error.OK && error != Error.ERROR_EXISTED && error != Error.ERROR_CLOUD_EXISTED_ERROR) {
                throw new RuntimeException("addPerson " + personName + " error:" + error);
            } else {
                printf("addPerson success: personName:" + person.name + " personId:" + person.id);
            }


            Feature feature = new Feature();
            feature.name = featureName;
            feature.feature = featureStr;
            error = faceRegister.addFeature(person.id, feature);
            if (error != Error.OK && error != Error.ERROR_EXISTED && error != Error.ERROR_CLOUD_EXISTED_ERROR) {
                throw new RuntimeException("addFeature " + featureName + " error:" + error);
            } else {
                printf("addFeature success: personName:" + personName + " featureId:" + feature.id + " featureName:" + feature.name);/**/
            }
        }
    }


    @Override
    public void onRecognized(Image image, RecognizeResult[] results) {
        printf("onRecognized");
        if (results != null) {
            for (int i = 0; i < results.length; i++) {
                printf("onRecognized, RecognizeResult[" + i + "]" + results[0]);
            }
        }

        mRecognizeResult = results;
    }

    private RecognizeResult getRecognizeResult(Face face) {
        if (mRecognizeResult == null) {
            return null;
        }

        for (int i = 0; i < mRecognizeResult.length; i++) {
            if (mRecognizeResult[i].trackId == face.trackId) {
                return mRecognizeResult[i];
            }
        }

        return null;
    }

    private Face getFace(RecognizeResult result) {
        if (faces == null) {
            return null;
        }

        for (int i = 0; i < faces.length; i++) {
            if (faces[i].trackId == result.trackId) {
                return faces[i];
            }
        }

        return null;
    }

    public static void main(String[] args) throws IOException {
        new FaceRecognize_RTSP_NoUI_Demo();
    }

    private static String getTime() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss.SSS");
        return sdf.format(date);
    }

    private static void printf(String log) {
        System.out.println(getTime() + " : " + log);
    }
}
