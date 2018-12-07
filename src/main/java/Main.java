// Sample Java code for user authorization

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;


public class Main {

	/** Application name. */
	private static final String APPLICATION_NAME = "API Sample";

	/** Directory to store user credentials for this application. */
	private static final java.io.File DATA_STORE_DIR = new java.io.File(
			System.getProperty("user.home"), ".credentials/java-youtube-api-tests");

	/** Global instance of the {@link FileDataStoreFactory}. */
	private static FileDataStoreFactory DATA_STORE_FACTORY;

	/** Global instance of the JSON factory. */
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	/** Global instance of the HTTP transport. */
	private static HttpTransport HTTP_TRANSPORT;

	/**
	 * Global instance of the scopes required by this quickstart.
	 * <p>
	 * If modifying these scopes, delete your previously saved credentials
	 * at ~/.credentials/drive-java-quickstart
	 */
	private static final Collection<String> SCOPES = Arrays.asList("https://www.googleapis.com/auth/youtube.readonly");

	static {
		try {
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Creates an authorized Credential object.
	 *
	 * @return an authorized Credential object.
	 * @throws IOException
	 */
	public static Credential authorize() throws IOException {
		//System.setProperty("GOOGLE_APPLICATION_CREDENTIALS", "src/resources/client_secret.json");
		System.out.println(new File(".").getAbsolutePath());
		return GoogleCredential.getApplicationDefault(HTTP_TRANSPORT, JSON_FACTORY).createScoped(SCOPES);
	}

	/**
	 * Build and return an authorized API client service, such as a YouTube
	 * Data API client service.
	 *
	 * @return an authorized API client service
	 * @throws IOException
	 */
	public static YouTube getYouTubeService() throws IOException {
		Credential credential = authorize();

		return new YouTube.Builder(
				HTTP_TRANSPORT, JSON_FACTORY, credential)
				.setApplicationName(APPLICATION_NAME)
				.build();
	}

	public static void main(String[] args) throws IOException {

		YouTube youtube = getYouTubeService();

		try {
			/*System.out.println(youtube.playlists().list("snippet")//.setChannelId("UCR-ENZ64WL1vB8KU4YzdmTQ")
					.setId("PLru1HAOKPcjJQr4lsCh2eU9F2lhSX23F_").execute().getItems().get(0).);*/
			//System.out.println(youtube.playlistItems().list("snippet").setPlaylistId("PLru1HAOKPcjJQr4lsCh2eU9F2lhSX23F_").execute());
			PlaylistItemListResponse vids = youtube.playlistItems().list("snippet").setPlaylistId("PLru1HAOKPcjJQr4lsCh2eU9F2lhSX23F_").execute();
			for (PlaylistItem item : vids.getItems()) {
				System.out.println(item.getSnippet().getDescription());
			}
		} catch (GoogleJsonResponseException e) {
			e.printStackTrace();
			System.err.println("There was a service error: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage());
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}