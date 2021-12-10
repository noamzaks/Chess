from os import listdir, mkdir
from os.path import join, exists
from shutil import rmtree

pieces = [("B", "bishop"), ("K", "king"), ("N", "knight"), ("P", "pawn"), ("Q", "queen"), ("R", "rook")]
colors = [("w", "white"), ("b", "black")]

if exists("output"):
	rmtree("output")
mkdir("output")

themes = listdir(".")
themes.remove("extract.py")
themes.remove("output")
themes.remove("mono")

for theme in themes:
	for color, target_color in colors:
		for piece, target_piece in pieces:
			svg = open(join(theme, color + piece + ".svg"), "r").read()
			open(join("output", theme  + "_" + target_color + "_" + target_piece + ".svg"), "w").write(svg)

template = """Map.entry("{0}", Map.of(
		Pawn.class, new int[]{{R.drawable.{1}_white_pawn, R.drawable.{1}_black_pawn}},
		Knight.class, new int[]{{R.drawable.{1}_white_knight, R.drawable.{1}_black_knight}},
		Bishop.class, new int[]{{R.drawable.{1}_white_bishop, R.drawable.{1}_black_bishop}},
		Rook.class, new int[]{{R.drawable.{1}_white_rook, R.drawable.{1}_black_rook}},
		Queen.class, new int[]{{R.drawable.{1}_white_queen, R.drawable.{1}_black_queen}},
		King.class, new int[]{{R.drawable.{1}_white_king, R.drawable.{1}_black_king}}
))"""

print("private static final List<String> THEMES = List.of(" + ", ".join(map(lambda theme: '"' + theme.capitalize() + '"', themes)) + ");")
print()
print("private static final Map<String, Map<Class<? extends Piece>, int[]>> RESOURCE_MAP = Map.ofEntries(" + ",\n".join(map(lambda theme: template.format(theme.capitalize(), theme), themes)) + "\n);")