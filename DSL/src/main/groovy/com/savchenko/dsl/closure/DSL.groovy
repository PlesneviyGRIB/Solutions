package com.savchenko.dsl.closure

import com.savchenko.dsl.config.GroupsLvlConfiguration
import lombok.Getter
import static groovy.lang.Closure.DELEGATE_ONLY

@Getter
class DSL{
    static EnvironmentParams environmentParams = new EnvironmentParams()
    static GroupsLvlConfiguration groupsLvlConfiguration = new GroupsLvlConfiguration()
    static Map<String, String> allData = new HashMap<>()

    static void environment(@DelegatesTo(value = EnvironmentParams, strategy = DELEGATE_ONLY) Closure closure){
        closure.delegate = environmentParams
        closure.resolveStrategy = DELEGATE_ONLY
        closure.call()
    }

    static void configuration(@DelegatesTo(value = GroupsLvlConfiguration, strategy = DELEGATE_ONLY) Closure closure){
        closure.delegate = groupsLvlConfiguration
        closure.resolveStrategy = DELEGATE_ONLY
        closure.call()
    }

    static void buildconfig(@DelegatesTo(value = BuildGroup, strategy = DELEGATE_ONLY) Closure closure){
        closure.delegate = new BuildGroup(environmentParams, groupsLvlConfiguration.groupConfiguration, allData)
        closure.resolveStrategy = DELEGATE_ONLY
        closure.call()
    }

    static void attributes(@DelegatesTo(value = Attributes, strategy = DELEGATE_ONLY) Closure closure){
        Attributes attributes = new Attributes(allData)
        closure.delegate = attributes
        closure.resolveStrategy = DELEGATE_ONLY
        closure.call()
    }

    static void makeHTMLresponse(){
        println('४०॰०॰०॰०॰०॰०॰०॰०॰०॰०॰०॰०॰०॰०॰०॰००॰०॰०॰०॰०॰०॰०॰०॰०॰०॰०॰०॰०॰०॰०॰०॰०॰०॰०४')
        println("html file location: \"${environmentParams.getHtmlResponseDirectory()}\"")

        println allData
    }
}

@Getter
class EnvironmentParams{
    String downloadDirectory = '/home/egor/tmp'
    String htmlResponseDirectory = '/home/egor/tmp/response'

    void downloadDirectory (String path){
        downloadDirectory = path
    }

    void htmlResponseDirectory (String path){
        htmlResponseDirectory = path
    }
}