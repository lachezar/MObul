package org.mobul.db;

import java.util.Date;

public class LoadFixtures {
	
	public static void loadDummyObservationEvents(AbstractObservationEvent oeProvider, int count) {
		oeProvider.deleteAll();
		
		for (int i = 0; i < count; i++) {
			oeProvider.insert(""+(0+i), "MObul MObul MObul MObul MObul MObul alpha test " + i,
				"This is simple demo of Massive Observation client for Android. " +
				"Please feel free to play with it and send us feedback or comment. " +
				"Thank you for participating in this project :-) ",
				"[{id: \"5\"," +
				"title: \"MObul alpha test "+i+"\"," +
				"description: \"This is simple demo of Massive Observation client for Android. Please feel free to play with it and send us feedback or comment. Thank you for participating in this project :-) \"," +
				"fields: [" +
					"{key: \"text1\", type: \"text\", description: \"Type some single-line text\", isMandatory: true}," +
					"{key: \"note2\", type: \"note\", description: \"Type some multi-line text\", isMandatory: true}," +
					"{key: \"photo3\", type: \"photo\", description: \"Take a photo\", isMandatory: true}," +
					"{key: \"geo4\", type: \"gps\", description: \"Take your GPS fix\", isMandatory: true}," +
					"{key: \"choice6\", type: \"choice\", description: \"Choose an option\", isMandatory: true, options: [ {id: 1, title: \"A\"}, {id: 2, title: \"B\"}, {id: 3, title: \"C\"}]}," +
					"{key: \"multichoice7\", type: \"multichoice\", description: \"Choose multiple options\", isMandatory: true, options: [ {id: 4, title: \"X\"}, {id: 5, title: \"Y\"}, {id: 6, title: \"Z\"}]}," +
					"{key: \"datetime11\", type: \"datetime\", description: \"Take current date and time\", isMandatory: true}" +
					"{key: 8, type: \"date\", description: \"Take current date\", isMandatory: true}," +
					"{key: 9, type: \"time\", description: \"Take current time\", isMandatory: true}," +
					"{key: 10, type: \"photo-gps\", description: \"Take a photo with GPS fix\", isMandatory: true}," +
					"{key: 11, type: \"photo-datetime\", description: \"Take a photo with the current date and time\", isMandatory: true}," +
					"{key: 12, type: \"photo-gps-datetime\", description: \"Take a photo with GPS fix and current date and time\", isMandatory: true}" +
				"]}" +
				"]",
				"lol, rofl, sofia",
				"Bulgaria - BG", "Sofia province", "Sofia",
				0L, new Date().getTime()/1000);
		}
		
	}
	
	/* The format of the OE is
	 * [
			{
				key: "key_1", 
				title: "title", 
				description: "...", 
				[ _TASKS_ ], 
				tags: "tag1, tag2, tagN", 
				country: "Bulgaria - BG", 
				province: "Sofia", 
				city: "Sofia", 
				version: 123456789
			},
			.....
		]
	 */

}
