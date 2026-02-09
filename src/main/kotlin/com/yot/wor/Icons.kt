package com.yot.wor

import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

object Icons {
    @JvmField
    val TOOL_WINDOW: Icon = IconLoader.findIcon("/icons/toolWindowIcon.svg", Icons::class.java)!!

    @JvmField
    val PLUGIN_ICON: Icon = IconLoader.findIcon("/icons/pluginIcon.svg", Icons::class.java)!!
}
