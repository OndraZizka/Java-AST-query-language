/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.queryToAST.app.Metadata;



import com.queryToAST.app.Core.TypFile;
import com.queryToAST.app.Graph.GraphContext;
import com.queryToAST.app.Metadata.ClassMetadata;
import com.queryToAST.app.Setting;
import com.strobel.assembler.metadata.JarTypeLoader;
import com.strobel.assembler.metadata.TypeDefinition;
import com.strobel.core.StringUtilities;
import com.strobel.decompiler.DecompilerSettings;
import com.strobel.decompiler.languages.Languages;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import com.tinkerpop.gremlin.java.GremlinPipeline;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;




/**
 *
 * @author Niriel
 */
public class JarMetadata {
    private Setting _settings = null;
    private GraphContext _graphContext = null;
    public JarMetadata(String _internalName, GraphContext graphContext) throws IOException {
        _graphContext = graphContext;
        _settings = new Setting(_internalName,null);
        _settings.setMetadata(true);
        execute();
    }


    /**
     *
     * @return
     * @throws IOException
     */
    private void execute() throws IOException {
        DecompilerSettings settings = DecompilerSettings.javaDefaults();
        settings.setLanguage(Languages.bytecode()); //metadata
        //settings.setLanguage(Languages.java()); // plna dekompilace a AST
        final File jarFile = new File(this._settings.getInternalName());
        if (!jarFile.exists()) {
            System.out.println("File not found: " + this._settings.getInternalName());
        }
        final JarFile jar = new JarFile(jarFile);
        final Enumeration<JarEntry> entries = jar.entries();        
        settings.setShowSyntheticMembers(false);
        settings.setTypeLoader(           
                new JarTypeLoader(jar)                            
        );        
        this._settings.setSettings(settings);        
        _graphContext.setName(jar.getName());
        try {                        
            while (entries.hasMoreElements()) {
                final JarEntry entry = entries.nextElement();
                final String name = entry.getName();
                if (!name.endsWith(".class")) {
                    continue;
                }
                final String internalName = StringUtilities.removeRight(name, ".class");
                this._settings.setInternalName(internalName);
                
                if(!internalName.contains("NormalTest"))
                    continue;
                ClassMetadata meta = new ClassMetadata(_settings);
                _graphContext.CreateClassMetadata(meta.getMetadata());
                
            }
        }
        finally {
         //System.out.println("Doplnit hlaseni p�ekladu JarMetadata");
        }
    }
    
}