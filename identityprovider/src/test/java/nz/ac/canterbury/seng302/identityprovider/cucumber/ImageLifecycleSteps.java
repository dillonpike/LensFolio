package nz.ac.canterbury.seng302.identityprovider.cucumber;


import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.grpc.stub.StreamObserver;
import nz.ac.canterbury.seng302.identityprovider.model.UserModel;
import nz.ac.canterbury.seng302.identityprovider.model.UserModelRepository;
import nz.ac.canterbury.seng302.identityprovider.service.UserAccountServerService;
import nz.ac.canterbury.seng302.shared.identityprovider.UploadUserProfilePhotoRequest;
import nz.ac.canterbury.seng302.shared.util.FileUploadStatus;
import nz.ac.canterbury.seng302.shared.util.FileUploadStatusResponse;
import org.mariadb.jdbc.MariaDbBlob;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Blob;

import static nz.ac.canterbury.seng302.shared.util.FileUploadStatus.*;

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

        StreamObserver<FileUploadStatusResponse> responseObserver = new StreamObserver<FileUploadStatusResponse>() {
            ByteArrayOutputStream imageArray = new ByteArrayOutputStream();
            FileUploadStatus fileUploadStatus = PENDING;
            boolean byteFailed = false;
            String message = "Byte uploading";
            int userId;
            String fileType;

            @Override
            public void onNext(FileUploadStatusResponse value) {
//                FileUploadStatusResponse.Builder reply = FileUploadStatusResponse.newBuilder();
//
//                if (value.hasMetaData()) {
//                    userId = value.getMetaData().getUserId();
//                    fileType = value.getMetaData().getFileType();
//                } else {
//                    try {
//                        imageArray.write(value.getFileContent().toByteArray());
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
//                Blob blob = new MariaDbBlob(imageArray.toByteArray());
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
