package org.mobul.oe.task;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mobul.db.DataHelper;
import org.mobul.db.ObservationData;
import org.mobul.utils.StringUtils;

import android.app.Activity;

public class TaskForm {

	public static TaskForm[] generateTasks(String jsonText, Activity activity)
			throws JSONException {

		JSONArray tasks = new JSONArray(jsonText);

		TaskForm[] oeTasks = new TaskForm[tasks.length()];
		for (int i = 0; i < oeTasks.length; i++) {
			oeTasks[i] = generateTask(tasks.getJSONObject(i), activity);
		}

		return oeTasks;
	}

	public static TaskForm generateTask(JSONObject task, Activity activity)
			throws JSONException {

		TaskForm oeTask = new TaskForm(activity);

		oeTask.setId(task.getLong("id"));
		oeTask.setTitle(task.getString("title"));
		oeTask.setDescription(task.getString("description"));

		JSONArray fields = task.getJSONArray("fields");
		Field[] taskFields = new Field[fields.length()];

		for (int i = 0; i < taskFields.length; i++) {
			taskFields[i] = generateField(fields.getJSONObject(i), activity, i);
		}

		oeTask.setFields(taskFields);

		return oeTask;
	}

	private static Field generateField(JSONObject field, Activity activity, int i) throws JSONException {

		String id = field.getString("key");
		String type = field.getString("type");
		String description = field.getString("description");
		boolean required = field.getBoolean("isMandatory");
		Field taskField = null;

		if (type.equals("text")) {
			taskField = new TextField(activity);
		} else if (type.equals("note")) {
			taskField = new NoteField(activity);
		} else if (type.equals("photo")) {
			taskField = new PhotoField(activity);
		} else if (type.equals("gps")) {
			taskField = new GPSField(activity);
		} else if (type.equals("choice")) {
			ChoiceField cf = new ChoiceField(activity);
			HashMap<Integer, String> choices = generateChoices(field, activity);
			cf.setChoices(choices);
			taskField = cf;
		} else if (type.equals("multichoice")) {
			MultiChoiceField cf = new MultiChoiceField(activity);
			HashMap<Integer, String> choices = generateChoices(field, activity);
			cf.setChoices(choices);
			taskField = cf;
		} else if (type.equals("datetime")) {
			taskField = new DateTimeField(activity);
		} else if (type.equals("date")) {
			taskField = new DateField(activity);
		} else if (type.equals("time")) {
			taskField = new TimeField(activity);
		} else if (type.equals("photo-gps")) {
			taskField = new PhotoGPSField(activity);
		} else if (type.equals("photo-datetime")) {
			taskField = new PhotoDateTimeField(activity);
		} else if (type.equals("photo-gps-datetime")) {
			taskField = new PhotoGPSDateTimeField(activity);
		} else if (type.equals("audio")) {
			taskField = new TextField(activity);
		}

		taskField.setId(id);
		taskField.setDescription(description);
		taskField.setType(type);
		taskField.setRequired(required);
		taskField.setIndex(i);

		return taskField;
	}

	private static HashMap<Integer, String> generateChoices(JSONObject field,
			Activity activity) throws JSONException {
		JSONArray choicesJSON = field.getJSONArray("options");
		HashMap<Integer, String> choices = new HashMap<Integer, String>();
		for (int i = 0; i < choicesJSON.length(); i++) {
			JSONObject choiceJSON = choicesJSON.getJSONObject(i);
			choices.put(choiceJSON.getInt("id"), choiceJSON.getString("title"));
		}
		return choices;
	}

	public TaskForm(Activity activity) {
		dh = new DataHelper(activity);
	}

	public boolean isValid() {
		for (Field f : fields) {
			if (!f.isValid()) {
				return false;
			}
		}

		return true;
	}

	public String serializeObservation() throws JSONException {
		List<String> fieldsText = new LinkedList<String>();
		for (Field f : fields) {
			fieldsText.add(f.toString());
		}

		return "[" + StringUtils.join(fieldsText, ", ") + "]";
	}

	public boolean saveObservation() {
		dh.getObservationData().deleteNotReadyByTaskId(id);
		return saveObservation(1L);
	}

	public boolean saveNotReadyObservation() {
		dh.getObservationData().deleteNotReadyByTaskId(id);
		return saveObservation(0L);
	}

	public boolean saveObservation(long state) {
		String serializedData = null;
		try {
			serializedData = serializeObservation();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		long dataSize = serializedData.getBytes().length;
		
		for(Field f : fields) {
			dataSize += f.getDataSize();
		}

		dh.getObservationData().insert(id, state, serializedData, new Date().getTime(), dataSize);

		return true;
	}

	public void deserializeObservation(String data) throws JSONException {
		JSONArray json = new JSONArray(data);
		
		if (fields.length != json.length()) {
			throw new JSONException("JSON data does not match the fields");
		}

		for (int i = 0; i < json.length(); i++) {
			fields[i].fromString(json.getString(i));
		}
	}

	public boolean retrieveLastObservation() {

		ObservationData.Model m = dh.getObservationData().selectByTaskIdAndNotReady(id);

		try {
			if (m == null) {
				return false;
			}

			deserializeObservation(m.serializedData);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}
	
	public void reset() {
		for (Field f : fields) {
			f.reset();
		}
	}

	private DataHelper dh;
	protected long id;
	protected String title;
	protected String description;
	protected Field[] fields;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Field[] getFields() {
		return fields;
	}

	public void setFields(Field[] fields) {
		this.fields = fields;
	}

}
