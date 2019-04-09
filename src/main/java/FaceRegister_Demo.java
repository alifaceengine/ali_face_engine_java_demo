/*
 * This Java source file was generated by the Gradle 'init' task.
 */

import com.alibaba.cloud.faceengine.Error;
import com.alibaba.cloud.faceengine.*;

public class FaceRegister_Demo {
    private static int RunMode = Mode.TERMINAL;
    //private static int RunMode = Mode.CLOUD;
    private static String GROUP_NAME = "组A";
    private static Group sGroup = new Group();

    public static void main(String[] args) {
        //step 1: authorize or enable debug
        FaceEngine.enableDebug(true);
        System.out.println("VENDOR_KEY : " + Utils.VENDOR_KEY);
        int error = FaceEngine.authorize(Utils.VENDOR_KEY);
        if (error != Error.OK) {
            System.out.println("authorize error : " + error);
            return;
        } else {
            System.out.println("authorize OK");
        }


        //step 2: set Cloud addr and account if you using CloudServer
        //FaceEngine.setCloudAddr("101.132.89.177", 15000);
        //FaceEngine.setCloudAddr("127.0.0.1", 8080);
        FaceEngine.setCloudLoginAccount("admin", "admin");


        //1:N face recognize
        FaceRegister faceRegister = FaceRegister.createInstance();

        //1:N, step1:register face
        registerPictures(faceRegister);


        //other face database management
        getAllGroups(faceRegister);
        getGroupInfo(faceRegister, sGroup.id);
        getAllPersons(faceRegister, sGroup.id);
        getPersonNum(faceRegister, sGroup.id);


        copyGroup(faceRegister);

        //release instance
        FaceRegister.deleteInstance(faceRegister);
    }


    private static final String BASE_PERSONS[] = {
            "liudehua_feature1.jpg", "liudehua_feature2.jpg",
            "zhangxueyou_feature1.jpg", "zhangxueyou_feature2.jpg"};
    private static final String TEST_PERSONS[] = {
            "liudehua.jpg",
            "zhangxueyou.jpg"};

    private static void registerPictures(FaceRegister faceRegister) {
        System.out.println("registerPictures begin");
        sGroup.name = GROUP_NAME;
        sGroup.modelType = ModelType.MODEL_SMALL;
        int error = faceRegister.createGroup(sGroup);
        if (error != Error.OK && error != Error.ERROR_EXISTED && error != Error.ERROR_CLOUD_EXISTED_ERROR) {
            throw new RuntimeException("createGroup " + GROUP_NAME + " error:" + error);
        } else {
            System.out.println("createGroup OK:" + error + " groupId:" + sGroup.id);
        }

        addPersonsAndFeatures(faceRegister, sGroup.id);
        System.out.println("registerPictures end\n\n======================");
    }

    private static void addPersonsAndFeatures(FaceRegister faceRegister, String groupId) {
        FaceDetect faceDetect = FaceDetect.createInstance(RunMode);
        if (faceDetect == null) {
            System.out.println("FaceDetect.createInstance error");
            return;
        }

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
            int error = faceRegister.addPerson(groupId, person);
            if (error != Error.OK && error != Error.ERROR_EXISTED && error != Error.ERROR_CLOUD_EXISTED_ERROR) {
                throw new RuntimeException("addPerson " + personName + " error:" + error);
            } else {
                System.out.println("addPerson success: personName:" + person.name + " personId:" + person.id);
            }


            Feature feature = new Feature();
            feature.name = featureName;
            feature.feature = featureStr;
            error = faceRegister.addFeature(person.id, feature);
            if (error != Error.OK && error != Error.ERROR_EXISTED && error != Error.ERROR_CLOUD_EXISTED_ERROR) {
                throw new RuntimeException("addFeature " + featureName + " error:" + error);
            } else {
                System.out.println("addFeature success: personName:" + personName + " featureId:" + feature.id + " featureName:" + feature.name);/**/
            }
        }

        FaceDetect.deleteInstance(faceDetect);
    }

    private static void getAllGroups(FaceRegister faceRegister) {
        System.out.println("getAllGroups begin");
        Group groupInfos[] = faceRegister.getAllGroups();
        if (groupInfos == null) {
            System.out.println("getAllGroupInfos : null");
        } else {
            for (int i = 0; i < groupInfos.length; i++) {
                System.out.println("getAllGroupInfos [" + i + "]=" + groupInfos[i]);
            }
        }
        System.out.println("getAllGroups end\n\n======================");
    }

    private static void getGroupInfo(FaceRegister faceRegister, String groupId) {
        System.out.println("getGroup begin:" + groupId);
        Group groupInfo = faceRegister.getGroup(groupId);
        System.out.println("getGroupInfo=" + groupInfo);
        System.out.println("getGroup end\n\n======================");
    }


    private static void getAllPersons(FaceRegister faceRegister, String groupId) {
        System.out.println("getAllPersons begin+++++++++++");
        Person persons[] = faceRegister.getAllPersons(groupId);
        if (persons == null) {
            System.out.println("getAllPersons =null");
        } else {
            for (int i = 0; i < persons.length; i++) {
                System.out.println("getAllPersons [" + i + "]=" + persons[i]);
            }
        }

        System.out.println("getAllPersons end\n\n------------");
    }

    private static void getPersonNum(FaceRegister faceRegister, String groupId) {
        System.out.println("getPersonNum begin+++++++++++");
        int num = faceRegister.getPersonNum(groupId);
        System.out.println("getPersonNum=" + num);
        System.out.println("getPersonNum end\n\n------------");
    }

    private static void copyGroup(FaceRegister faceRegister) {
        System.out.println("copyGroup begin+++++++++++");
        Group newGroup = new Group();
        newGroup.name = "NewGroup";
        newGroup.modelType = sGroup.modelType;
        int error = faceRegister.createGroup(newGroup);
        if (error != Error.OK && error != Error.ERROR_EXISTED && error != Error.ERROR_CLOUD_EXISTED_ERROR) {
            throw new RuntimeException("copyGroup " + GROUP_NAME + " error:" + error);
        } else {
            System.out.println("copyGroup OK:" + error + " groupId:" + sGroup.id);
        }

        Person persons[] = faceRegister.getAllPersons(sGroup.id);
        if (persons == null) {
            return;
        }

        for (int i = 0; i < persons.length; i++) {
            Person personCopy = new Person();
            personCopy.name = persons[i].name;
            error = faceRegister.addPerson(newGroup.id, personCopy);
            if (error != Error.OK && error != Error.ERROR_EXISTED && error != Error.ERROR_CLOUD_EXISTED_ERROR) {
                throw new RuntimeException("copyGroup addPerson" + personCopy.name + " error:" + error);
            }

            if (persons[i].features == null || persons[i].features.length == 0) {
                continue;
            }

            for (int j = 0; j < persons[i].features.length; j++) {
                Feature feature = new Feature();
                feature.name = persons[i].features[j].name;
                feature.feature = persons[i].features[j].feature;
                error = faceRegister.addFeature(personCopy.id, feature);
                if (error != Error.OK && error != Error.ERROR_EXISTED && error != Error.ERROR_CLOUD_EXISTED_ERROR) {
                    throw new RuntimeException("copyGroup addFeature" + feature.name + " error:" + error);
                }
            }
        }


        System.out.println("copyGroup getAllPersons begin+++++++++++");
        Person personsCopy[] = faceRegister.getAllPersons(newGroup.id);
        if (personsCopy == null) {
            System.out.println("copyGroup getAllPersons personsCopy=null");
        } else {
            for (int i = 0; i < personsCopy.length; i++) {
                System.out.println("copyGroup getAllPersons [" + i + "]=" + personsCopy[i]);
            }
        }

        System.out.println("copyGroup getAllPersons end\n\n------------");
    }
}
