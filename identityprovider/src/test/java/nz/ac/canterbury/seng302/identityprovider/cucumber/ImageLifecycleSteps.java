package nz.ac.canterbury.seng302.identityprovider.cucumber;


import com.google.protobuf.ByteString;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.grpc.stub.StreamObserver;
import nz.ac.canterbury.seng302.identityprovider.model.UserModel;
import nz.ac.canterbury.seng302.identityprovider.model.UserModelRepository;
import nz.ac.canterbury.seng302.identityprovider.service.UserAccountServerService;
import nz.ac.canterbury.seng302.shared.identityprovider.ProfilePhotoUploadMetadata;
import nz.ac.canterbury.seng302.shared.identityprovider.UploadUserProfilePhotoRequest;
import nz.ac.canterbury.seng302.shared.util.FileUploadStatus;
import nz.ac.canterbury.seng302.shared.util.FileUploadStatusResponse;
import org.mariadb.jdbc.MariaDbBlob;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.Blob;

import static nz.ac.canterbury.seng302.shared.util.FileUploadStatus.*;

@SpringBootTest
public class ImageLifecycleSteps {

    @Autowired
    @MockBean
    private UserModelRepository userModelRepository;

    @Autowired
    @MockBean
    private UserAccountServerService userAccountServerService;

    UserModel user;

    @Given("I have a user in the database")
    public void i_have_a_user_in_the_database() {
        user = new UserModel(
                "test",
                "test",
                "test",
                "middle",
                "last",
                "nickname",
                "test@testing.com",
                "bio",
                "they/them"
        );
        user.setUserId(0);

        userModelRepository = Mockito.mock(UserModelRepository.class);
        Mockito.when(userModelRepository.save(user)).thenReturn(user);
    }

    @When("I upload an image")
    public void i_upload_an_image() {

        byte[] imageArray = new byte[0];
        try {
            BufferedImage testImage = ImageIO.read(new File("test/resources/exampleFiles/test_image_1.jpg"));
            ByteArrayOutputStream imageArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(testImage, "jpg", imageArrayOutputStream);
            imageArray = imageArrayOutputStream.toByteArray();
        } catch (IOException e) {
            System.err.println("You didn't find the image correctly");
        }
        byte[] finalImageArray = imageArray;



        StreamObserver<FileUploadStatusResponse> responseObserver = new StreamObserver<FileUploadStatusResponse>() {
            int lengthOfImageArray = finalImageArray.length;
            int currentImageArrayCount = 0;
            boolean allImageStreamed = false;

            boolean hasStreamedMetaData = false;

            FileUploadStatus latestFileUploadStatus = PENDING;


            @Override
            public void onNext(FileUploadStatusResponse value) {
                UploadUserProfilePhotoRequest.Builder reply = UploadUserProfilePhotoRequest.newBuilder();

                reply.setMetaData(ProfilePhotoUploadMetadata.newBuilder().setUserId(user.getUserId()).setFileType("jpg").build());


                switch (value.getStatusValue()) {
                    case 0:  // PENDING
                        reply.setMetaData(ProfilePhotoUploadMetadata.newBuilder().setUserId(user.getUserId()).setFileType("jpg").build());
                        //.onNext(reply.build());
                        break;

                    case 1:  // IN_PROGRESS
                        reply.setFileContent(ByteString.copyFrom(ByteBuffer.allocateDirect(finalImageArray[currentImageArrayCount])));
                        currentImageArrayCount++;
                        if (currentImageArrayCount == lengthOfImageArray) {
                            allImageStreamed = true;
                        }
                        break;

                    case 2:  // SUCCESS
                        break;

                    case 3:  // FAILED
                        break;

                }

                if (allImageStreamed) {
                    ;
                }

//                if (value.hasMetaData()) {
//                    userId = value.getMetaData().getUserId();
//                    fileType = value.getMetaData().getFileType();
//                } else {
//                    try {
//                        finalImageArray.write(value.getFileContent().toByteArray());
//                        fileUploadStatus = IN_PROGRESS;
//                        message = "Byte uploading";
//                        responseObserver.onNext(reply.setStatus(fileUploadStatus).setMessage(message).build());
//                    } catch (IOException e) {
//                        fileUploadStatus = FAILED;
//                        message = "Byte failed to write to OutputStream";
//                        byteFailed = true;
//                        responseObserver.onNext(reply.setStatus(fileUploadStatus).setMessage(message).build());
//                        e.printStackTrace();
//                    }
//                }
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("Failed to stream image");
            }

            @Override
            public void onCompleted() {
//                FileUploadStatusResponse.Builder reply = FileUploadStatusResponse.newBuilder();
//
//                //  Somehow call the function below (savePhotoToUser)
//                Blob blob = new MariaDbBlob(finalImageArray.toByteArray());
//                boolean wasSaved = false;
//                if (byteFailed) {
//                    //wasSaved = savePhotoToUser(userId, blob);
//                }
//
//                if (wasSaved) {
//                    message = "Image saved to database";
//                    fileUploadStatus = SUCCESS;
//                } else {
//                    message = "Image failed to save to database";
//                    fileUploadStatus = FAILED;
//                }
//
//                responseObserver.onNext(reply.setStatus(fileUploadStatus).setMessage(message).build());
//                responseObserver.onCompleted();
            }
        };

        StreamObserver<UploadUserProfilePhotoRequest> reply = userAccountServerService.uploadUserProfilePhoto(responseObserver);

    }
    @Then("The image is saved persistently")
    public void the_image_is_saved_persistently() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

}
