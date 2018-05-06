package com.shaishavgandhi.navigator;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

@AutoService(Processor.class)
public class ExtraProcessor extends AbstractProcessor {

    private static final ClassName context = ClassName.get("android.content", "Context");
    private static final ClassName intent = ClassName.get("android.content", "Intent");
    private HashMap<String, Set<Element>> annotationsPerClass;

    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
        annotationsPerClass = new HashMap<>();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        for (Element element : roundEnvironment.getElementsAnnotatedWith(Extra.class)) {
            String classname = element.getEnclosingElement().getSimpleName().toString();
            if (annotationsPerClass.containsKey(classname)) {
                Set<Element> annotations = annotationsPerClass.get(classname);
                annotations.add(element);
                annotationsPerClass.put(classname, annotations);
            } else {
                Set<Element> annotations = new HashSet<>();
                annotations.add(element);
                annotationsPerClass.put(classname, annotations);
            }
        }


        TypeSpec.Builder navigator = TypeSpec.classBuilder("Navigator")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

        for (Map.Entry<String, Set<Element>> item : annotationsPerClass.entrySet()) {
            String activity = item.getKey();
            Set<Element> annotations = item.getValue();
            MethodSpec method = getMethod(activity, annotations);
            navigator.addMethod(method);
        }

        JavaFile javaFile = JavaFile.builder("com.shaishavgandhi.navigator", navigator.build())
                .build();

        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    private MethodSpec getMethod(String activity, Set<Element> elements) {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("start" + activity)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC)
                .addParameter(context, "context");

        builder.addStatement("$T intent = new $T($L, $L)", intent, intent, "context",
                activity + ".class");

        for (Element element: elements) {
            TypeMirror typeMirror = element.asType();
            if (typeMirror == null) {
                continue;
            }
            String name = element.getSimpleName().toString();
            builder.addParameter(TypeName.get(typeMirror), name);
            builder.addStatement("intent.putExtra(\"$L\", $L)", name, name);
        }

        builder.addStatement("$L.startActivity($L)", "context", "intent");
        return builder.build();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> set = new HashSet<>();
        set.add(Extra.class.getCanonicalName());
        return set;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }
}
