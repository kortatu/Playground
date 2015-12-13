module Main where
import Data.List
import Geometry


solve points = perimeter (convexHull points)

perimeter :: [Point] -> Float
perimeter points = perimeter' points + distanceP (head points) (last points)

perimeter' :: [Point] -> Float
perimeter' (x:[]) = 0.0
perimeter' (x:y:xs) = distance x y + perimeter' (y:xs)
 

convexHull :: [Point] -> [Point]
convexHull points =  [point1] ++ processSemiPlane High seg fp1 ++ [point2] ++ reverse (processSemiPlane Low seg fp2)
  where hList = sortBy (horizontalSort) points	 
        point1 = head hList
        point2 = last hList
        seg = Segment point1 point2
        sp1 = semiPlaneS seg High
        sp2 = semiPlaneS seg Low
        fp1 = filterSp sp1 points
        fp2 = filterSp sp2 points

processSemiPlane :: Direction -> Segment -> [Point] -> [Point]
processSemiPlane _ _ [] = []
processSemiPlane dir seg@(Segment p1 p2) points =  processSemiPlane dir seg1 fp1 ++ [selectedPoint] ++ processSemiPlane dir seg2 fp2
    where selectedPoint = last (sortBy (sortByDistance (lineFromSegment seg)) points)
          seg1 = (Segment p1 selectedPoint)
          seg2 = (Segment selectedPoint p2)
          sp1 = semiPlaneT p1 selectedPoint p2
          sp2 = semiPlaneT selectedPoint p2 p1
          fp1 = filterSp sp1 points
          fp2 = filterSp sp2 points
                    
filterSp sp = filter (inSemiplaneStrict sp) 
          
sortByDistance :: Line -> Point -> Point -> Ordering
sortByDistance l p p' 
		      | distance l p > distance l p' = GT
		      | otherwise = LT

convexHullLog :: [Point] -> IO [Point]
convexHullLog points = do
    points1 <-processSemiPlaneLog High seg fp1
    points2 <-processSemiPlaneLog Low seg fp2
    return ([point1] ++ points1 ++ [point2] ++ reverse points2)
   where hList = sortBy (horizontalSort) points
         point1 = head hList
         point2 = last hList
         seg = Segment point1 point2
         sp1 = semiPlaneS seg High
         sp2 = semiPlaneS seg Low
         fp1 = filterSp sp1 points
         fp2 = filterSp sp2 points

processSemiPlaneLog :: Direction -> Segment -> [Point] -> IO [Point]
processSemiPlaneLog _ _ [] = return []
processSemiPlaneLog dir seg@(Segment p1 p2) points =  do
                                                    print ("Semiplane first half "++show fp1)
                                                    print selectedPoint
                                                    print ("Second semiplane "++show sp2)
                                                    print ("Semiplane second half "++show fp2)
                                                    return $ processSemiPlane dir seg1 fp1 ++ [selectedPoint] ++ processSemiPlane dir seg2 fp2
    where selectedPoint = last (sortBy (sortByDistance (lineFromSegment seg)) points)
          seg1 = (Segment p1 selectedPoint)
          seg2 = (Segment selectedPoint p2)
          sp1 = semiPlaneT p1 selectedPoint p2
          sp2 = semiPlaneT selectedPoint p2 p1
          fp1 = filterSp sp1 points
          fp2 = filterSp sp2 points


	
main :: IO ()
main = do
  n <- readLn :: IO Int
  content <- getContents
  result <-convexHullLog (map (\[x, y] -> Point x y). map (map (read::String->Float)). map words. lines $ content)
  print result
--  let
--    points = map (\[x, y] -> Point x y). map (map (read::String->Float)). map words. lines $ content
--    result = solve points
--    ch = convexHull points
--  print ch
--  print result