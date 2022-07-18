package com.fypmoney.view.arcadegames.model

sealed class ArcadeType{
    data class SPIN_WHEEL(var productCode:String):ArcadeType()
    data class SCRATCH_CARD(var productCode:String):ArcadeType()
    data class SLOT(var productCode:String):ArcadeType()
    data class TREASURE_BOX(var productCode:String):ArcadeType()
    object NOTypeFound:ArcadeType()
}
fun checkTheArcadeType(arcadeType: String,productCode: String):ArcadeType{
    when(arcadeType){
        "SPIN_WHEEL"->{
            return ArcadeType.SPIN_WHEEL(productCode)
        }
        "SCRATCH_CARD"->{
            return ArcadeType.SCRATCH_CARD(productCode)

        }
        "SLOT"->{
            return ArcadeType.SLOT(productCode)

        }
        "TREASURE_BOX"->{
            return ArcadeType.TREASURE_BOX(productCode)
        }else->{
        return ArcadeType.NOTypeFound
     }
    }
}
//SPIN_WHEEL,SCRATCH_CARD,SLOT,TREASURE_BOX