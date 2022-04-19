package dev.fastriver.fluko.framework.painting

import dev.fastriver.fluko.common.Size
import kotlin.math.min

enum class BoxFit {
    Fill, Contain, Cover, FitWidth, FitHeight, None, ScaleDown;

    data class FittedSizes(
        val source: Size,
        val destination: Size,
    )

    companion object {
        /**
         * BoxFitを適用する
         *
         * 外側と内側でScaleが異なる可能性があるため、返り値の大きさは一致するとは限らない
         *
         * @param fit 適用するBoxFitの種類
         * @param inputSize 内側(Fitされる)の矩形のサイズ
         * @param outputSize 外側(描画部分)の矩形のサイズ
         *
         * @return 2つのSizeを返す。[FittedSizes.source]: inputSizeの内表示される部分のSize。[FittedSizes.destination]: outputSizeの内使われる部分のSize。
         */
        fun applyBoxFit(fit: BoxFit, inputSize: Size, outputSize: Size): FittedSizes {
            if(inputSize.height <= 0.0 || inputSize.width <= 0.0 || outputSize.height <= 0.0 || outputSize.width <= 0.0) {
                return FittedSizes(Size.zero, Size.zero)
            }

            val sourceSize: Size
            var destinationSize: Size
            when(fit) {
                Fill -> {
                    sourceSize = inputSize
                    destinationSize = outputSize
                }
                Contain -> {
                    sourceSize = inputSize
                    destinationSize = if (outputSize.width / outputSize.height > sourceSize.width / sourceSize.height) {
                        // 外側の方が横長の場合
                        Size(sourceSize.width * outputSize.height / sourceSize.height, outputSize.height)
                    } else {
                        // 外側のほうが縦長の場合
                        Size(outputSize.width, sourceSize.height * outputSize.width / sourceSize.width)
                    }
                }
                Cover -> {
                    sourceSize = if (outputSize.width / outputSize.height > inputSize.width / inputSize.height) {
                        // 外側のほうが横長の場合
                        Size(inputSize.width, inputSize.width * outputSize.height / outputSize.width)
                    } else {
                        // 外側のほうが縦長の場合
                        Size(inputSize.height * outputSize.width / outputSize.height, inputSize.height)
                    }
                    destinationSize = outputSize
                }
                FitWidth -> {
                    sourceSize = Size(inputSize.width, inputSize.width * outputSize.height / outputSize.width)
                    destinationSize = Size(outputSize.width, sourceSize.height * outputSize.width / sourceSize.width)
                }
                FitHeight -> {
                    sourceSize = Size(inputSize.height * outputSize.width / outputSize.height, inputSize.height)
                    destinationSize = Size(sourceSize.width * outputSize.height / sourceSize.height, outputSize.height)
                }
                None -> {
                    sourceSize = Size(min(inputSize.width, outputSize.width), min(inputSize.height, outputSize.height))
                    destinationSize = sourceSize
                }
                ScaleDown -> {
                    sourceSize = inputSize
                    destinationSize = inputSize
                    val aspectRatio = inputSize.width / inputSize.height
                    if(destinationSize.height > outputSize.height) {
                        destinationSize = Size(outputSize.height * aspectRatio, outputSize.height)
                    }
                    if(destinationSize.width > outputSize.width) {
                        destinationSize = Size(outputSize.width, outputSize.width / aspectRatio)
                    }
                }
            }
            return FittedSizes(sourceSize, destinationSize)
        }
    }
}
