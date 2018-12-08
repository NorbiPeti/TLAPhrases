// Sample Java code for user authorization

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Main {

	/** Application name. */
	private static final String APPLICATION_NAME = "API Sample";

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
			final Supplier<YouTube.PlaylistItems.List> getReq = () ->
			{
				try {
					return youtube.playlistItems().list("snippet,contentDetails").setPlaylistId("PLru1HAOKPcjJQr4lsCh2eU9F2lhSX23F_")
							.setFields("items(contentDetails/videoId,snippet/title,snippet/description),nextPageToken").setMaxResults(50L);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			};
			PlaylistItemListResponse vids = getReq.get().execute();
			//final Pattern pat = Pattern.compile("[-\u00AD]{10,}\\n.*[Cc]omment.*\"(.+)\".*[^-]*[-\u00AD]{10,}");
			final Pattern pat = Pattern.compile("[Cc]omment[,\\s.:]*[\"'“]((?:\\w|\\s|[^\"”.])+)[\"'”.]|\\(If you (?:read|see) this, comment: (.*)\\)");
			int C = 0, CV = 0;
			do {
				for (PlaylistItem item : vids.getItems()) {
					CV++;
					String desc = item.getSnippet().getDescription();
					Matcher matcher = pat.matcher(desc);
					String phrase = null;
					while (matcher.find()) { //There may be other "comment xy" messages but this should be always the last one
						phrase = matcher.group(1);
						if (phrase == null) phrase = matcher.group(2);
						phrase = phrase.replace("\"", "").trim(); //Easier this way
					}
					if (phrase == null) {
						//System.out.println("\n\n\n\n"+desc+"\n\n\n\n");
						continue;
					}
					//System.out.println(phrase + "\t" + item.getSnippet().getTitle()+"\t"+"https://www.youtube.com/watch?v="+item.getContentDetails().getVideoId());
					System.out.println(phrase + "\t" + item.getSnippet().getTitle() + "\t" + item.getContentDetails().getVideoId());
					/*if (item.getSnippet().getTitle().contains("Rude - MAGIC!")) System.out.println(desc);
					int ind = desc.indexOf(SEPARATOR_STR);
					int ind2 = desc.indexOf(SEPARATOR_STR, ind + SEPARATOR_STR.length());
					if (item.getSnippet().getTitle().contains("Rude - MAGIC!")) {
						System.out.println("Ind: " + ind + " " + ind2);
						int x = desc.indexOf("-------------------");
						System.out.println("- ind: " + x);
						for (int i = x; i < desc.length(); i++) System.out.print(" " + (int) desc.charAt(i));
					}
					if (ind == -1 || ind2 == -1) continue;
					String section = desc.substring(ind, ind2);
					if (!section.toLowerCase().contains("comment")) continue;
					ind = section.indexOf("\"");
					ind2 = section.indexOf("\"", ind + 1);
					if (ind == -1 || ind2 == -1) continue;
					String word = section.substring(ind + 1, ind2);
					System.out.println(word);
					if (word.equals("Without Warning")) System.out.println(section);
					if (word.equals("Teddy bears are my friends")) System.out.println(item.getSnippet().getTitle());
					if (word.equals("#BuyLegacyOniTunes")) System.out.println(item.getSnippet().getTitle());
					if (word.equals("HALO")) System.out.println(item.getSnippet().getTitle());*/
					C++;
				}
				if (vids.getNextPageToken() == null) break;
				vids = getReq.get().setPageToken(vids.getNextPageToken()).execute();
			} while (true);
			System.out.println("\nWords/phrases: " + C + "\nVideos: " + CV);
		} catch (GoogleJsonResponseException e) {
			e.printStackTrace();
			System.err.println("There was a service error: " + e.getDetails().getCode() + " : " + e.getDetails().getMessage());
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}