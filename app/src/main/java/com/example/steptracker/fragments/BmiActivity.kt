package com.example.steptracker

import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.Anchor
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.HitTestResult
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.activity_bmi.*

class BmiActivity : AppCompatActivity() {
    private lateinit var fragment: ArFragment
    private var isTracking: Boolean = false
    private var isHitting: Boolean = false
    private var itentData: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bmi)
        //get data from intent
        val intent = intent
        val bmiInterpret = intent.getStringExtra("bmiTxt")
        if (bmiInterpret != null) {
            itentData = bmiInterpret
        }
        setSupportActionBar(findViewById(R.id.toolbarBmi))
        //actionbar
        val actionbar = supportActionBar
        // set actionbar title
        actionbar!!.title = getString(R.string.bmi_result)
        // set back button
        actionbar.setDisplayHomeAsUpEnabled(true)

        fragment = sceneform_fragment as ArFragment
        fragment.arSceneView.scene.addOnUpdateListener { frameTime ->
            fragment.onUpdate(frameTime)
            onUpdate()
        }

        floatingActionButton.setOnClickListener { addObject(Uri.parse("CupCakeFinal2.sfb")) }
        showFab(false)

    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun showFab(enabled: Boolean) {
        if (enabled) {
            floatingActionButton.isEnabled = true
            floatingActionButton.visibility = View.VISIBLE
        } else {
            floatingActionButton.isEnabled = false
            floatingActionButton.visibility = View.GONE
        }
    }

    private fun onUpdate() {
        updateTracking()
        if (isTracking) {
            val hitTestChanged = updateHitTest()
            if (hitTestChanged) {
                showFab(isHitting)
            }
        }

    }

    private fun updateHitTest(): Boolean {
        val frame = fragment.arSceneView.arFrame
        val point = getScreenCenter()
        val hits: List<HitResult>
        val wasHitting = isHitting
        isHitting = false
        if (frame != null) {
            hits = frame.hitTest(point.x.toFloat(), point.y.toFloat())
            for (hit in hits) {
                val trackable = hit.trackable
                if (trackable is Plane && trackable.isPoseInPolygon(hit.hitPose)) {
                    isHitting = true
                    break
                }
            }
        }
        return wasHitting != isHitting
    }

    private fun updateTracking(): Boolean {
        val frame = fragment.arSceneView.arFrame
        val wasTracking = isTracking
        if (frame != null) {
            isTracking = frame.camera.trackingState == TrackingState.TRACKING
        }
        return isTracking != wasTracking
    }

    private fun getScreenCenter(): Point {
        val vw = findViewById<View>(android.R.id.content)
        return Point(vw.width / 2, vw.height / 2)
    }


    private fun addObject(model: Uri) {
        val frame = fragment.arSceneView.arFrame
        val pt = getScreenCenter()
        if (frame != null) {
            val hits = frame.hitTest(pt.x.toFloat(), pt.y.toFloat())
            for (hit in hits) {
                val trackable = hit.trackable
                if (trackable is Plane && trackable.isPoseInPolygon(hit.hitPose)) {
                    placeObject(fragment, hit.createAnchor(), model)
                    break
                }
            }
        }
    }

    private fun placeObject(fragment: ArFragment, anchor: Anchor, model: Uri) {
        ModelRenderable.builder()
            .setSource(fragment.context, model)
            .build()
            .thenAccept {
                addNodeToScene(fragment, anchor, it)
            }
            .exceptionally {
                return@exceptionally null
            }
    }


    private fun addNodeToScene(fragment: ArFragment, anchor: Anchor, renderable: ModelRenderable) {
        val anchorNode = AnchorNode(anchor)
        val viewNode = TransformableNode(fragment.transformationSystem)
        viewNode.renderable = renderable
        viewNode.setParent(anchorNode)
        fragment.arSceneView.scene.addChild(anchorNode)
        viewNode.select()
        viewNode.setOnTapListener{ hitTestRes: HitTestResult?, motionEv: MotionEvent? ->
            showFab(false)
        }
    }
}