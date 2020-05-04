package com.example.processor;

import com.google.auto.service.AutoService;
import com.sun.source.util.Trees;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Objects;
import java.util.Set;

/**
 * @author bfsan
 * @since 2020/4/22
 **/
@SupportedAnnotationTypes(value = {"com.example.processor.AutoProvided"})
@SupportedSourceVersion(value = SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class MyProcessor extends AbstractProcessor {

    private TreeMaker treeMaker;
    private Name.Table names;
    private Context context;
    private Trees trees;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            return true;
        }
        final Messager messager = processingEnv.getMessager();
        trees = Trees.instance(processingEnv);

        context = ((JavacProcessingEnvironment) processingEnv).getContext();
        treeMaker = TreeMaker.instance(context);
        names = Names.instance(context).table;

        messager.printMessage(Diagnostic.Kind.NOTE, "开始处理AutoProvided");

        for (Element root : roundEnv.getRootElements()) {
            //收集标记注解的element & construct
            ElementCollector elementCollector = new ElementCollector().init();
            elementCollector.scan(root);

            //为构造方法增加代码内容
            elementCollector.getConstructs()
                    .parallelStream()
                    .filter(Objects::nonNull)
                    .forEach(construct -> {
                        JCTree.JCMethodDecl methodDecl = (JCTree.JCMethodDecl) trees.getTree(construct);


                        ListBuffer<JCTree.JCStatement> statements = new ListBuffer<>();
                        statements.addAll(methodDecl.body.getStatements());
                        statements.add(treeMaker.Exec(treeMaker.Apply(
                                List.of(memberAccess("java.lang.String")),
                                memberAccess("java.lang.System.out.println"),
                                List.of(treeMaker.Literal("Hello world"))
                        )));
                        methodDecl.body = treeMaker.at(methodDecl.getPreferredPosition()).Block(0, statements.toList());
                    });

            System.out.println(1);
        }

        //结束处理
        return true;
    }

    private Name getNameFromString(String s) {
        return names.fromString(s);
    }

    private JCTree.JCExpression memberAccess(String components) {
        String[] componentArray = components.split("\\.");
        JCTree.JCExpression expr = treeMaker.Ident(getNameFromString(componentArray[0]));
        for (int i = 1; i < componentArray.length; i++) {
            expr = treeMaker.Select(expr, getNameFromString(componentArray[i]));
        }
        return expr;
    }

}
