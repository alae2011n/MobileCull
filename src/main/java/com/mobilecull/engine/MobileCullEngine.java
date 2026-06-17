package com.mobilecull.engine;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import org.joml.Matrix4f;

public class MobileCullEngine {
    public static final MobileCullEngine INSTANCE = new MobileCullEngine();
    
    private final ExecutorService workerThread = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "MobileCull-Worker");
        thread.setPriority(Thread.NORM_PRIORITY);
        thread.setDaemon(true);
        return thread;
    });

    private final float[] activeFrustumPlanes = new float[24];
    private final float[] workerFrustumPlanes = new float[24];
    private final Object lock = new Object();
    private final AtomicBoolean isProcessing = new AtomicBoolean(false);

    public boolean cullEntities = true;
    public boolean cullBlockEntities = true;
    public boolean cullParticles = true;
    public boolean aggressiveChunkCull = false;

    public void updateFrustum(Matrix4f modelViewProjection) {
        synchronized (lock) {
            activeFrustumPlanes[0] = modelViewProjection.m03() + modelViewProjection.m00();
            activeFrustumPlanes[1] = modelViewProjection.m13() + modelViewProjection.m10();
            activeFrustumPlanes[2] = modelViewProjection.m23() + modelViewProjection.m20();
            activeFrustumPlanes[3] = modelViewProjection.m33() + modelViewProjection.m30();
            
            activeFrustumPlanes[4] = modelViewProjection.m03() - modelViewProjection.m00();
            activeFrustumPlanes[5] = modelViewProjection.m13() - modelViewProjection.m10();
            activeFrustumPlanes[6] = modelViewProjection.m23() - modelViewProjection.m20();
            activeFrustumPlanes[7] = modelViewProjection.m33() - modelViewProjection.m30();

            activeFrustumPlanes[8] = modelViewProjection.m03() + modelViewProjection.m01();
            activeFrustumPlanes[9] = modelViewProjection.m13() + modelViewProjection.m11();
            activeFrustumPlanes[10] = modelViewProjection.m23() + modelViewProjection.m21();
            activeFrustumPlanes[11] = modelViewProjection.m33() + modelViewProjection.m31();

            activeFrustumPlanes[12] = modelViewProjection.m03() - modelViewProjection.m01();
            activeFrustumPlanes[13] = modelViewProjection.m13() - modelViewProjection.m11();
            activeFrustumPlanes[14] = modelViewProjection.m23() - modelViewProjection.m21();
            activeFrustumPlanes[15] = modelViewProjection.m33() - modelViewProjection.m31();

            activeFrustumPlanes[16] = modelViewProjection.m03() + modelViewProjection.m02();
            activeFrustumPlanes[17] = modelViewProjection.m13() + modelViewProjection.m12();
            activeFrustumPlanes[18] = modelViewProjection.m23() + modelViewProjection.m22();
            activeFrustumPlanes[19] = modelViewProjection.m33() + modelViewProjection.m32();

            activeFrustumPlanes[20] = modelViewProjection.m03() - modelViewProjection.m02();
            activeFrustumPlanes[21] = modelViewProjection.m13() - modelViewProjection.m12();
            activeFrustumPlanes[22] = modelViewProjection.m23() - modelViewProjection.m22();
            activeFrustumPlanes[23] = modelViewProjection.m33() - modelViewProjection.m32();

            for (int i = 0; i < 6; i++) {
                int offset = i * 4;
                float length = (float) Math.sqrt(
                    activeFrustumPlanes[offset] * activeFrustumPlanes[offset] +
                    activeFrustumPlanes[offset + 1] * activeFrustumPlanes[offset + 1] +
                    activeFrustumPlanes[offset + 2] * activeFrustumPlanes[offset + 2]
                );
                if (length != 0.0f) {
                    activeFrustumPlanes[offset] /= length;
                    activeFrustumPlanes[offset + 1] /= length;
                    activeFrustumPlanes[offset + 2] /= length;
                    activeFrustumPlanes[offset + 3] /= length;
                }
            }
        }
    }

    public boolean isBoxVisible(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        synchronized (lock) {
            System.arraycopy(activeFrustumPlanes, 0, workerFrustumPlanes, 0, 24);
        }

        for (int i = 0; i < 6; i++) {
            int offset = i * 4;
            double pX = workerFrustumPlanes[offset];
            double pY = workerFrustumPlanes[offset + 1];
            double pZ = workerFrustumPlanes[offset + 2];
            double pW = workerFrustumPlanes[offset + 3];

            double extremeX = (pX >= 0) ? maxX : minX;
            double extremeY = (pY >= 0) ? maxY : minY;
            double extremeZ = (pZ >= 0) ? maxZ : minZ;

            if (pX * extremeX + pY * extremeY + pZ * extremeZ + pW < 0) {
                return false;
            }
        }
        return true;
    }

    public void queueAsyncCalculation(Runnable task) {
        if (isProcessing.compareAndSet(false, true)) {
            workerThread.submit(() -> {
                try {
                    task.run();
                } finally {
                    isProcessing.set(false);
                }
            });
        }
    }
    }
