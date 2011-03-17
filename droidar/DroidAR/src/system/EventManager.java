package system;

import geo.GeoObj;
import geo.GeoUtils;

import java.util.HashMap;
import java.util.List;

import listeners.EventListener;
import actions.EventListenerGroup;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

import commands.Command;

/**
 * this EventManager is attached to the main {@link Thread} and should react on
 * any kind of event or input
 * 
 * @author Spobo
 * 
 */

public class EventManager implements LocationListener, SensorEventListener {

	private static final String LOG_TAG = "Event Manager";

	private static final long MIN_MS_BEFOR_UPDATE = 1000;
	private static final float MIN_DIST_FOR_UPDATE = 1;

	private static EventManager myInstance = new EventManager();

	// all the predefined actions:
	/**
	 * this action will be executed when the user moves the finger over the
	 * screen
	 */
	// private EventAction onTouchMoveAction;
	public EventListener onTrackballEventAction;
	public EventListener onOrientationChangedAction;
	public EventListener onLocationChangedAction;
	public HashMap<Integer, Command> myOnKeyPressedCommandList;

	// public static final boolean USE_ACCEL_AND_MAGNET = true;
	// final float[] inR = new float[16];
	// float[] orientation = new float[3];

	// private float clickPossible = 0;
	// private float[] gravityValues = new float[3];
	// private float[] geomagneticValues = new float[3];

	private GeoObj currentLocation;

	private Activity myTargetActivity;

	public static EventManager getInstance() {
		return myInstance;
	} 

	public void registerListeners(Activity targetActivity,
			boolean useAccelAndMagnetoSensors) {
		myTargetActivity = targetActivity;
		registerSensorUpdates(targetActivity, useAccelAndMagnetoSensors);
		registerLocationUpdates(targetActivity);

	}

	private void registerSensorUpdates(Activity myTargetActivity,
			boolean useAccelAndMagnetoSensors) {
		SensorManager sensorManager = (SensorManager) myTargetActivity
				.getSystemService(Context.SENSOR_SERVICE);

		if (useAccelAndMagnetoSensors) {
			/*
			 * To register the EventManger for magnet- and accelerometer-sensor
			 * events, two Sensor-objects have to be obtained and then the
			 * EventManager is set as the Listener for these type of sensor
			 * events. The update rate is set by SENSOR_DELAY_GAME to a high
			 * frequency required to react on fast device movement
			 */
			Sensor magnetSensor = sensorManager
					.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
			sensorManager.registerListener(this, magnetSensor,
					SensorManager.SENSOR_DELAY_GAME);
			Sensor accelSensor = sensorManager
					.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			sensorManager.registerListener(this, accelSensor,
					SensorManager.SENSOR_DELAY_GAME);
		} else {
			// Register orientation Sensor Listener:
			Sensor orientationSensor = sensorManager
					.getDefaultSensor(Sensor.TYPE_ORIENTATION);
			sensorManager.registerListener(this, orientationSensor,
					SensorManager.SENSOR_DELAY_GAME);
		}
	}

	private void registerLocationUpdates(Activity myTargetActivity) {
		LocationManager locationManager = (LocationManager) myTargetActivity
				.getSystemService(Context.LOCATION_SERVICE);
		Log.i(LOG_TAG, "Got locationmanager: " + locationManager);

		try {
			/*
			 * To register the EventManager in the LocationManager a Criteria
			 * object has to be created and as the primary attribute accuracy
			 * should be used to get as accurate position data as possible:
			 */

			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_FINE);

			String provider = locationManager.getBestProvider(criteria, true);
			if (provider == null) {
				Log.w(LOG_TAG, "No location-provider with the "
						+ "specified requierments found.. Trying to find "
						+ "an alternative.");
				List<String> providerList = locationManager.getProviders(true);
				for (String possibleProvider : providerList) {
					if (possibleProvider != null) {
						Log.w(LOG_TAG, "Location-provider alternative "
								+ "found: " + possibleProvider);
						provider = possibleProvider;
					}
				}
				if (provider == null)
					Log.w(LOG_TAG, "No location-provider alternative "
							+ "found!");
			}

			locationManager.requestLocationUpdates(provider,
					MIN_MS_BEFOR_UPDATE, MIN_DIST_FOR_UPDATE, this);
		} catch (Exception e) {
			Log.e(LOG_TAG, "There was an error registering the "
					+ "EventManger for location-updates. The phone might be "
					+ "in airplane-mode..");
			e.printStackTrace();
		}
	}

	public void onAccuracyChanged(Sensor s, int accuracy) {
		// Log.d("sensor onAccuracyChanged", arg0 + " " + arg1);
	}

	public void onSensorChanged(SensorEvent event) {
		if (onOrientationChangedAction != null) {

			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				onOrientationChangedAction.onAccelChanged(event.values);
			}
			if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
				onOrientationChangedAction.onMagnetChanged(event.values);
			}

			// else sensor input is set to orientation mode
			if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
				onOrientationChangedAction.onOrientationChanged(
						event.values[1], event.values[2], event.values[0]);
			}
		}
	}

	/*
	 * // TODO test what happens when its a 3x3 matrix (better // performance?):
	 * 
	 * // load inR matrix from current sensor data:
	 * SensorManager.getRotationMatrix(inR, null, gravityValues,
	 * geomagneticValues);
	 * 
	 * SensorManager.getOrientation(inR, orientation);
	 * mapMagAndAcclDataToVector(orientation); orientetionChanged(orientation);
	 */

	// private void mapMagAndAcclDataToVector(float[] values) {
	// // degree=radians*180/PI
	// values[0] = values[0] * 360 / 3.141592653589793f;
	// values[1] = values[1] * 180 / 3.141592653589793f;
	// values[2] = values[2] * -180 / 3.141592653589793f;
	// }

	public void onLocationChanged(Location location) {
		if (onLocationChangedAction != null) {
			onLocationChangedAction.onLocationChanged(location);
		}
	}

	public void onProviderDisabled(String provider) {

	}

	public void onProviderEnabled(String provider) {

	}

	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	public void addOnOrientationChangedAction(EventListener action) {
		Log.d(LOG_TAG, "Adding onOrientationChangedAction");
		onOrientationChangedAction = addActionToTarget(
				onOrientationChangedAction, action);
	}

	public void addOnTrackballAction(EventListener action) {
		Log.d(LOG_TAG, "Adding onTouchMoveAction");
		onTrackballEventAction = addActionToTarget(onTrackballEventAction,
				action);

	}

	public void addOnLocationChangedAction(EventListener action) {
		Log.d(LOG_TAG, "Adding onLocationChangedAction");
		onLocationChangedAction = addActionToTarget(onLocationChangedAction,
				action);
	}

	public static EventListener addActionToTarget(EventListener target,
			EventListener action) {
		if (target == null) {
			target = action;
			Log.d(LOG_TAG, "Setting target command to " + action + "");
		} else if (target instanceof EventListenerGroup) {
			((EventListenerGroup) target).add(action);
			Log.d(LOG_TAG, "Adding " + action + " to existing actiongroup.");
		} else {
			EventListenerGroup g = new EventListenerGroup();
			g.add(target);
			g.add(action);
			target = g;
			Log.d(LOG_TAG, "Adding " + action + " to new actiongroup.");
		}
		return target;
	}

	/**
	 * @param actionToRemove
	 *            the {@link EventListener} to remove
	 * @param actionToInsert
	 *            set it to null to just remove the {@link EventListener}-object
	 * @return true if the actionToRemove-EventListener could be removed
	 */
	public boolean exchangeOnTrackballEventAction(EventListener actionToRemove,
			EventListener actionToInsert) {

		if (onTrackballEventAction instanceof EventListenerGroup) {
			return exchangeAction((EventListenerGroup) onTrackballEventAction,
					actionToRemove, actionToInsert);
		} else if (actionToRemove == onTrackballEventAction) {
			onTrackballEventAction = actionToInsert;
			return true;
		}
		return false;
	}

	/**
	 * @param actionToRemove
	 *            the {@link EventListener} to remove
	 * @param actionToInsert
	 *            set it to null to just remove the {@link EventListener}-object
	 * @return true if the actionToRemove-EventListener could be removed
	 */
	public boolean exchangeOnOrientationChangedAction(
			EventListener actionToRemove, EventListener actionToInsert) {
		if (onOrientationChangedAction instanceof EventListenerGroup) {
			return exchangeAction(
					(EventListenerGroup) onOrientationChangedAction,
					actionToRemove, actionToInsert);
		} else if (actionToRemove == onOrientationChangedAction) {
			onOrientationChangedAction = actionToInsert;
			return true;
		}
		return false;
	}

	/**
	 * @param actionToRemove
	 *            the {@link EventListener} to remove
	 * @param actionToInsert
	 *            set it to null to just remove the {@link EventListener}-object
	 * @return true if the actionToRemove-EventListener could be removed
	 */
	public boolean exchangeOnLocationChangedAction(
			EventListener actionToRemove, EventListener actionToInsert) {
		if (onLocationChangedAction instanceof EventListenerGroup) {
			return exchangeAction((EventListenerGroup) onLocationChangedAction,
					actionToRemove, actionToInsert);
		} else if (actionToRemove == onLocationChangedAction) {
			onLocationChangedAction = actionToInsert;
			return true;
		}
		return false;
	}

	/**
	 * @param targetGroup
	 * @param actionToRemove
	 * @param actionToInsert
	 *            set it to null to just remove the {@link EventListener}-object
	 */
	private boolean exchangeAction(EventListenerGroup targetGroup,
			EventListener actionToRemove, EventListener actionToInsert) {
		if (actionToInsert != null)
			targetGroup.add(actionToInsert);
		return targetGroup.remove(actionToRemove);
	}

	public void addOnKeyPressedCommand(int keycode, Command c) {
		if (myOnKeyPressedCommandList == null)
			myOnKeyPressedCommandList = new HashMap<Integer, Command>();
		myOnKeyPressedCommandList.put(keycode, c);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode >= 19 && keyCode <= 22) {
			/*
			 * if the keycode is on of the numbers from 19 to 22 it is a pseudo
			 * trackball event (eg the motorola milestone has pseudo trackball).
			 * here hare the codes (lets hope they are the same on each phone;):
			 * 
			 * top=19 down=20 left=21 right=22
			 */
			if (onTrackballEventAction != null) {
				final float stepLength = 0.3f;
				float x = 0, y = 0;
				switch (keyCode) {
				case 19:
					y = -stepLength;
					break;
				case 20:
					y = stepLength;
					break;
				case 21:
					x = -stepLength;
					break;
				case 22:
					x = stepLength;
					break;
				}
				return onTrackballEventAction.onTrackballEvent(x, y, null);
			}

			return false;
		}

		if (myOnKeyPressedCommandList == null)
			return false;
		Command commandForThisKey = myOnKeyPressedCommandList.get(keyCode);
		if (commandForThisKey != null) {
			Log.d("Command", "Key with command was pressed so executing "
					+ commandForThisKey);
			return commandForThisKey.execute();
		}
		return false;
	}

	/**
	 * This is the default method to get the position of the device.
	 * 
	 * @return an {@link GeoObj} which will be always at the current location of
	 *         the device. The virtual camera is using this object for example
	 *         to always display the virtual overlay in a correct way.
	 * 
	 *         There are two other methods you might want to take a loog at
	 *         {@link #getAutoupdatingCurrentLocationObjectFromSystem()} and
	 *         {@link #getNewCurrentLocationObjectFromSystem()}
	 */
	public GeoObj getCurrentLocationObject() {
		if (currentLocation == null) {
			return getAutoupdatingCurrentLocationObjectFromSystem();
		}
		return currentLocation;
	}

	/**
	 * @return always the same (but updated) object which will always hold the
	 *         current device location and which will auto-update itself
	 */
	private GeoObj getAutoupdatingCurrentLocationObjectFromSystem() {

		// l1 will be the more accurate position so first try l1:
		// TODO l1 might be much older then l2 maybe encapsulate this in an
		// method and return the more up to date location??

		Location l1 = GeoUtils.getCurrentLocation(myTargetActivity);
		if (l1 != null) {
			return assignLocationToGeoObj(l1);
		}

		Log.e(LOG_TAG, "Couldn't receive Location object for current location");
		if (currentLocation == null)
			currentLocation = new GeoObj(false);
		return currentLocation;// TODO return null; instead?
	}

	/**
	 * The Android system will be asked directly and if the external location
	 * manager knows where the device is located at the moment, this location
	 * will be returned
	 * 
	 * @return a new {@link GeoObj} or null if there could be no current
	 *         location calculated
	 */
	public GeoObj getNewCurrentLocationObjectFromSystem() {
		return getAutoupdatingCurrentLocationObjectFromSystem().copy();
	}

	private GeoObj assignLocationToGeoObj(Location l) {
		if (currentLocation == null) {
			currentLocation = new GeoObj(l, false);
			// currentLocation.getMyInfoObject().setShortDescr(CURR_LOC_DESCR);
		} else
			currentLocation.setLocation(l);
		return currentLocation;
	}

	public boolean onTrackballEvent(MotionEvent event) {
		if (onTrackballEventAction != null) {
			return onTrackballEventAction.onTrackballEvent(event.getX(),
					event.getY(), event);
		}
		return false;
	}

	public void setCurrentLocation(Location location) {
		currentLocation.setLocation(location);
	}

	public static void resetInstance() {
		myInstance = new EventManager();
	}

}