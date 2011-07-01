package gl;

import javax.microedition.khronos.opengles.GL10;

import util.EfficientList;
import util.Vec;
import worldData.Obj;
import worldData.Visitor;
import android.util.Log;

public class MeshGroup extends MeshComponent {

	public EfficientList<MeshComponent> myMeshes = new EfficientList<MeshComponent>();

	public MeshGroup() {
		super(null);
	}

	/**
	 * @param pos
	 *            not side effect free (working with pos afterwards will change
	 *            myPosition of this {@link MeshGroup})!
	 */
	public MeshGroup(Color c, Vec pos) {
		super(c);
		myPosition = pos;
	}

	public MeshGroup(Color color) {
		super(color);
	}

	public void add(MeshComponent x) {
		if (x == null) {
			Log.e("MeshGroup", "The mesh which should be added was NULL");
			return;
		}
		if (x == this) {
			Log.e("MeshGroup", "Endless recursion! Mesh cant be own parent.");
			return;
		}
		myMeshes.add(x);
		x.setMyParentMesh(this);
	}

	public boolean remove(MeshComponent x) {
		x.setMyParentMesh(null);
		return myMeshes.remove(x);
	}

	// @Override
	// public void setParent(MeshComponent m) {
	// // all children have to be informed:
	// for (MeshComponent c : myMeshes) {
	// c.setParent(this); // just inform them and dont update parent!
	// }
	// super.setParent(m);
	// }

	@Override
	public void draw(GL10 gl) {
		for (int i = 0; i < myMeshes.myLength; i++) {
			myMeshes.get(i).setMatrixAndDraw(gl);
		}
	}

	@Override
	public void update(float timeDelta, Obj obj) {
		if (graficAnimationActive) {
			for (int i = 0; i < myMeshes.myLength; i++) {
				myMeshes.get(i).update(timeDelta, obj);
			}
			// additionally update the own animations too:
			super.update(timeDelta, obj);
		}
	}

	public boolean accept(Visitor visitor) {
		return visitor.default_visit(this);
	}

	@Override
	public String toString() {
		if (myMeshes == null)
			return "Meshgroup (emtpy) " + super.toString();
		return "Meshgroup (size=" + myMeshes.myLength + ") " + super.toString();
	}

	public void clear() {
		for (int i = 0; i < myMeshes.myLength; i++) {
			myMeshes.get(i).setMyParentMesh(null);
		}
		myMeshes.clear();
	}

}
