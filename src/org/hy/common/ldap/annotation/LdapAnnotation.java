package org.hy.common.ldap.annotation;

import java.lang.annotation.ElementType;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.hy.common.ClassInfo;
import org.hy.common.ClassReflect;
import org.hy.common.Help;
import org.hy.common.MethodReflect;
import org.hy.common.PartitionMap;
import org.hy.common.xml.XJava;





/**
 * Ladp注解解释类
 *
 * @author      ZhengWei(HY)
 * @createDate  2017-02-14
 * @version     v1.0
 */
public class LdapAnnotation
{
    
    public static final String $LdapClassesXID = "LdapClasses";
    
    
    
    public static void parser()
    {
        PartitionMap<ElementType ,ClassInfo> v_Annotations     = ClassReflect.getAnnotations(Help.getClasses() ,Ldap.class);
        List<ClassInfo>                      v_ClassInfos      = null;
        ClassInfo                            v_ClassInfo       = null;
        Ldap                                 v_AnnoObjectClass = null;
        Map<Class<?> ,LdapEntry>             v_LdapEntrys      = new Hashtable<Class<?> ,LdapEntry>();
        LdapEntry                            v_LdapEntry       = null;
        
        if ( !Help.isNull(v_Annotations) )
        {
            // Ldap.objectClass  注解功能的实现
            v_ClassInfos = v_Annotations.get(ElementType.TYPE);
            if ( !Help.isNull(v_ClassInfos) )
            {
                for (int i=0; i<v_ClassInfos.size(); i++)
                {
                    v_ClassInfo       = v_ClassInfos.get(i);
                    v_AnnoObjectClass = v_ClassInfo.getClassObj().getAnnotation(Ldap.class);
                    v_LdapEntry       = new LdapEntry(Help.NVL(v_AnnoObjectClass.objectClass() ,v_AnnoObjectClass.value()));
                    
                    if ( !Help.isNull(v_ClassInfo.getFields()) )
                    {
                        // Java对象属性：Ldap.name  注解功能的实现
                        for (int x=0; x<v_ClassInfo.getFields().size(); x++)
                        {
                            Field  v_Field    = v_ClassInfo.getFields().get(x);
                            Method v_Method   = MethodReflect.getGetMethod(v_ClassInfo.getClassObj() ,v_Field.getName() ,true);
                            Ldap   v_AnnoAttr = v_Field.getAnnotation(Ldap.class);
                            
                            if ( v_Method != null )
                            {
                                v_LdapEntry.putElement(Help.NVL(v_AnnoAttr.name() ,Help.NVL(v_AnnoAttr.value() ,v_Field.getName())) ,v_Method);
                            }
                        }
                    }
                    
                    if ( !Help.isNull(v_ClassInfo.getMethods()) )
                    {
                        // Java对象方法：Ldap.name  注解功能的实现
                        for (int x=0; x<v_ClassInfo.getMethods().size(); x++)
                        {
                            Method v_Method   = v_ClassInfo.getMethods().get(x);
                            Ldap   v_AnnoAttr = v_Method.getAnnotation(Ldap.class);
                            String v_Name     = Help.NVL(v_AnnoAttr.name() ,v_AnnoAttr.value());
                            
                            if ( Help.isNull(v_Name) )
                            {
                                v_Name = v_Method.getName();
                                
                                if ( v_Name.startsWith("get") )
                                {
                                    v_Name = v_Name.substring(3);
                                }
                                else if ( v_Name.startsWith("is") )
                                {
                                    v_Name = v_Name.substring(2);
                                }
                            }
                            
                            v_LdapEntry.putElement(v_Name ,v_Method);
                        }
                    }
                    
                    // 只保存有对象类和属性的LDAP条目信息
                    if ( !Help.isNull(v_LdapEntry.getObjectClasses()) 
                      && !Help.isNull(v_LdapEntry.getElements()) )
                    {
                        v_LdapEntrys.put(v_ClassInfo.getClassObj() ,v_LdapEntry);
                    }
                }
            }
        }
        
        XJava.putObject($LdapClassesXID ,v_LdapEntrys);
    }
    
}