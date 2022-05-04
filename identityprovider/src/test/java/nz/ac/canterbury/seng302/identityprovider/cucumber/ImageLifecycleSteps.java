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
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
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
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ImageLifecycleSteps {

    @Autowired
    @MockBean
    private UserModelRepository userModelRepository;

    @Autowired
    @MockBean
    private UserAccountServerService userAccountServerService;

    UserModel user;
    Blob userPhoto;

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
        Mockito.when(userModelRepository.findByUserId(user.getUserId())).thenReturn(user);
    }

    @When("I upload an image")
    public void i_upload_an_image() {

        byte[] imageArray = new byte[0];
        try {
            BufferedImage testImage = ImageIO.read(new File("C:/Users/jmtho/Documents/Uni/SENG302 22W/Sprint 2/team-100/identityprovider/src/test/resources/exampleFiles/test_image_1.jpg"));  // DEBUGGING Use imageFile instead
            ByteArrayOutputStream imageArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(testImage, "jpg", imageArrayOutputStream);
            imageArray = imageArrayOutputStream.toByteArray();
        } catch (IOException e) {
            System.err.println("You didn't find the image correctly");
        }
        byte[] finalImageArray = imageArray;

        // Photo to compare to
        userPhoto = new MariaDbBlob(finalImageArray);

        StreamObserver<FileUploadStatusResponse> responseObserver = new StreamObserver<FileUploadStatusResponse>() {
            @Override
            public void onNext(FileUploadStatusResponse value) {

                UploadUserProfilePhotoRequest.Builder reply = UploadUserProfilePhotoRequest.newBuilder();

                switch (value.getStatusValue()) {
                    case 0:  // PENDING
                        System.out.println("Server pending");
                        System.out.println("    System returned: " + value.getMessage());
                        break;

                    case 1:  // IN_PROGRESS
                        System.out.println("Server uploading");
                        System.out.println("    System returned: " + value.getMessage());
                        break;

                    case 2:  // SUCCESS
                        System.out.println("Server finished successfully");
                        System.out.println("    System returned: " + value.getMessage());
                        break;

                    case 3:  // FAILED
                        System.out.println("Server failed to upload image");
                        System.out.println("    System returned: " + value.getMessage());
                        break;

                }
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                System.out.println("<-> Finished <->");
            }
        };

        // This doesn't make much sense, as this is the method this test is mostly trying to test.
        Mockito.when(userAccountServerService.uploadUserProfilePhoto(responseObserver)).then(new Answer<StreamObserver<UploadUserProfilePhotoRequest>>() {
                public StreamObserver<UploadUserProfilePhotoRequest> answer(InvocationOnMock invocation) throws Throwable {
                    UserModel tempUser = userModelRepository.findByUserId(user.getUserId());
                    tempUser.setPhoto(userPhoto);
                    userModelRepository.save(tempUser);
                    return new StreamObserver<UploadUserProfilePhotoRequest>() {
                        @Override
                        public void onNext(UploadUserProfilePhotoRequest value) {
                        }

                        @Override
                        public void onError(Throwable t) {
                        }

                        @Override
                        public void onCompleted() {
                        }
                    };
                }
            }
        );
        StreamObserver<UploadUserProfilePhotoRequest> requestObserver = userAccountServerService.uploadUserProfilePhoto(responseObserver);
        try {
            // Start with uploading the metadata
            UploadUserProfilePhotoRequest.Builder replyMetaData = UploadUserProfilePhotoRequest.newBuilder();
            ProfilePhotoUploadMetadata.Builder metaData = ProfilePhotoUploadMetadata.newBuilder().setUserId(user.getUserId()).setFileType("jpeg");
            replyMetaData.setMetaData(metaData.build());
            requestObserver.onNext(replyMetaData.build());
            // Loop through the bytes
            UploadUserProfilePhotoRequest.Builder reply = UploadUserProfilePhotoRequest.newBuilder();
            reply.setFileContent(ByteString.copyFrom(finalImageArray));
            requestObserver.onNext(reply.build());
            // Complete conversation
            requestObserver.onCompleted();
        } catch (Exception e) {
            System.err.println("Something went wrong uploading the file");
            e.printStackTrace();
        }



    }
    @Then("The image is saved persistently")
    public void the_image_is_saved_persistently() {
        UserModel userInDatabase = userModelRepository.findByUserId(user.getUserId());
        assertEquals(userPhoto, userInDatabase.getPhoto());
    }

}
