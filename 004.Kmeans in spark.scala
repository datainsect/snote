

class Point(_x:Double,_y:Double) with java.io.Serializable{
   val x = _x;
   val y = _y;
   override def toString = x +","+y;
}

def distance(p1:Point,p2:Point):Double = {
  math.pow((p1.x-p2.x),2) + math.pow((p1.y-p2.y),2);
}

val centerNum = 3;
val maxIter = 500;
val inputs = sc.textFile("2DMatrix.txt").filter(_.length>0);
val points = inputs.map{
  line =>
    val splited = line.split("\t");
    new Point(splited(0).toDouble,splited(0).toDouble)
}


def initialCenter(centerNums:Int,minPoint:Point,maxPoint:Point):Array[Point] = {
  val centers = new Array[Point](centerNums);
  val stepX = (maxPoint.x - minPoint.x)/centerNums;
  val stepY = (maxPoint.y - minPoint.y)/centerNums;

  for(i<- 0 until centerNums){
    val x = (i+ math.random) * stepX + minPoint.x;
    val y = (i+ math.random) * stepY + minPoint.y;
    centers(i) = new Point(x,y);
  }
  return centers;
}
