package nz.ac.canterbury.seng302.portfolio.utility;

import nz.ac.canterbury.seng302.portfolio.model.NotificationResponse;
import org.springframework.ui.Model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ToastUtility {

    private ToastUtility() {}

    public static void addToastsToModel(Model model, List<NotificationResponse> artefactsToDisplay, Integer numberOfToasts) {
        List<Toast> toastsToGenerate = new ArrayList<>();
        for (int i = 0; i < numberOfToasts; i++) {
            Toast toast = new Toast();
            toastsToGenerate.add(toast);
        }

        // Runs if the reload was triggered by saving an event. Checks the notifications' creation time to see if 2 seconds has passed yet.
        int count = 0;
        ArrayList<NotificationResponse> eventsToDelete = new ArrayList<>();
        for (NotificationResponse artefact : artefactsToDisplay) {
            long timeDifference = Date.from(Instant.now()).toInstant().getEpochSecond() - artefact.getDateOfCreation();
            if (timeDifference <= 5) {
                toastsToGenerate.get(count).setArtefactInformation(artefact.getArtefactType());
                toastsToGenerate.get(count).setArtefactName(artefact.getArtefactName());
                toastsToGenerate.get(count).setArtefactId(artefact.getArtefactId());
                toastsToGenerate.get(count).setUsername(artefact.getUsername());
                toastsToGenerate.get(count).setUserFirstName(artefact.getUserFirstName());
                toastsToGenerate.get(count).setUserLastName(artefact.getUserLastName());
                toastsToGenerate.get(count).setAction(artefact.getAction());
            } else {
                eventsToDelete.add(artefact);
                toastsToGenerate.get(count).setArtefactInformation("");
            }
            count++;
        }
        for (NotificationResponse event : eventsToDelete) {
            artefactsToDisplay.remove(event);
        }

        model.addAttribute("toastsToGenerate", toastsToGenerate);
    }
}
