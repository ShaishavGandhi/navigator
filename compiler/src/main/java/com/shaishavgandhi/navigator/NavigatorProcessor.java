package com.shaishavgandhi.navigator;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public final class NavigatorProcessor extends AbstractProcessor {

    private LinkedHashMap<String, Set<Element>> annotationsPerClass;
    private Types typeUtils;
    private Elements elementUtils;
    private Messager messager;

    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        messager = processingEnvironment.getMessager();
        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnvironment.getFiler();
        annotationsPerClass = new LinkedHashMap<>();
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


        FileWriter writer = new FileWriter(typeUtils, elementUtils, annotationsPerClass);
        JavaFile javaFile = writer.writeFile();

        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
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
