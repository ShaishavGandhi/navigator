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
import java.util.LinkedHashSet;
import java.util.List;
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
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public final class NavigatorProcessor extends AbstractProcessor {

    private LinkedHashMap<ClassName, LinkedHashSet<Element>> annotationsPerClass;
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

            Set<Modifier> modifiers = element.getModifiers();
            if (modifiers.contains(Modifier.FINAL)) {
                messager.printMessage(Diagnostic.Kind.ERROR, "Cannot set annotation @Extra on final variable");
            }

            ClassName className = getClassName(element);
            if (annotationsPerClass.containsKey(className)) {
                LinkedHashSet<Element> annotations = annotationsPerClass.get(className);
                annotations.add(element);
                annotationsPerClass.put(className, annotations);
            } else {
                LinkedHashSet<Element> annotations = new LinkedHashSet<>();
                annotations.add(element);
                annotationsPerClass.put(className, annotations);
            }
        }

        FileWriter writer = new FileWriter(typeUtils, elementUtils, annotationsPerClass, messager);
        writer.writeFiles();

        List<JavaFile> files = writer.getFiles();
        for (JavaFile file: files) {
            try {
                file.writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        annotationsPerClass.clear();

        return true;
    }

    private ClassName getClassName(Element element) {
        String classname = element.getEnclosingElement().getSimpleName().toString();
        String packageName;
        Element enclosing = element;
        while (enclosing.getKind() != ElementKind.PACKAGE) {
            enclosing = enclosing.getEnclosingElement();
        }
        PackageElement packageElement = ((PackageElement) enclosing);
        packageName = packageElement.toString();

        return ClassName.bestGuess(packageName + "." + classname);
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
