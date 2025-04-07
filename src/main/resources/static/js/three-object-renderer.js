import * as THREE from "https://cdn.jsdelivr.net/npm/three@0.121.1/build/three.module.js";
import { GLTFLoader } from "https://cdn.jsdelivr.net/npm/three@0.121.1/examples/jsm/loaders/GLTFLoader.js";

function init3DModel(containerSelector, modelPath, initialPosition, initialRotation, scaleFactor) {
    const container = document.querySelector(containerSelector);
    if (!container) {
        console.error(`Element ${containerSelector} was not found!`);
        return;
    }

    const scene = new THREE.Scene();
    const camera = new THREE.PerspectiveCamera(75, container.clientWidth / container.clientHeight, 0.1, 1000);
    const renderer = new THREE.WebGLRenderer({ antialias: true, alpha: true });

    renderer.setSize(container.clientWidth, container.clientHeight);
    container.appendChild(renderer.domElement);

    const ambientLight = new THREE.AmbientLight(0xffffff, 1);
    scene.add(ambientLight);

    const directionalLight = new THREE.DirectionalLight(0xffffff, 2);
    directionalLight.position.set(5, 5, 5);
    scene.add(directionalLight);

    const loader = new GLTFLoader();
    loader.load(modelPath, function (gltf) {
        const model = gltf.scene;
        scene.add(model);

        model.position.set(...initialPosition);
        model.rotation.y = initialRotation;
        model.scale.set(scaleFactor, scaleFactor, scaleFactor);

        model.traverse((node) => {
            if (node.isMesh) {
                node.castShadow = false;
                node.receiveShadow = false;
            }
        });

        function rotateModel() {
            model.rotation.y += 0.01;
            requestAnimationFrame(rotateModel);
        }
        rotateModel();
    }, undefined, function (error) {
        console.error(`Error while loading model: ${modelPath}:`, error);
    });

    camera.position.set(0, 0, 3);
    camera.lookAt(0, 0, 0);

    function animate() {
        requestAnimationFrame(animate);
        renderer.render(scene, camera);
    }
    animate();

    window.addEventListener('resize', function () {
        camera.aspect = container.clientWidth / container.clientHeight;
        camera.updateProjectionMatrix();
        renderer.setSize(container.clientWidth, container.clientHeight);
    });
}

init3DModel('.obiect-3d', '/3d-objects/newspaper/scene.gltf', [0, -1, -0.9], Math.PI / 4, 2);
init3DModel('.object', '/3d-objects/tv/scene.gltf', [0, -1, -1.2], Math.PI / 6, 1.25);
