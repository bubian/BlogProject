### svg标准的指令字母是10个，外加1个非标准的
[参考：深度掌握SVG路径path的贝塞尔曲线指令](http://www.zhangxinxu.com/wordpress/2014/06/deep-understand-svg-path-bezier-curves-command/)

命令 | 名称 | 参数
:-----| :---- | :----: |
M | moveto  移动到 | (x y)+ |
Z | closepath  关闭路径 | (none) |
L | lineto  画线到 | (x y)+ |
H | horizontal lineto  水平线到 | x+ |
V | vertical lineto  垂直线到 | y+ |
C | curveto  三次贝塞尔曲线到 | (x1 y1 x2 y2 x y)+ |
S | smooth curveto  光滑三次贝塞尔曲线到 | (x2 y2 x y)+ |
Q | quadratic Bézier curveto  二次贝塞尔曲线到 | (x1 y1 x y)+ |
T | smooth quadratic Bézier curveto  光滑二次贝塞尔曲线到 | (x y)+ |
A | elliptical arc  椭圆弧 | (rx ry x-axis-rotation large-arc-flag sweep-flag x y)+ |
R | Catmull-Rom curveto*  Catmull-Rom曲线 | x1 y1 (x y)+ |

- 说明

    其中，Catmull-Rom曲线不是标准的SVG命令，如果指令字母是大写的，例如M, 则表示坐标位置是绝对位置；如果指令字母小写的，例如m, 则表示坐标位置是相对位置。


