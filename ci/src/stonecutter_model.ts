import { z } from "zod";

export const SCProjectSlugSchema = z.string().transform((value) => {
  const [project, version, buildscript] = value.split(":", 3);
  return {
    project: project ?? value,
    version: version ?? null,
    buildscript: buildscript ?? null,
  };
});

export const SCProjectsByVersionSchema = z.record(
  z.string(),
  z.array(z.string()),
);

export type SCProjectSlug = z.infer<typeof SCProjectSlugSchema>;
export type SCProjectsByVersion = z.infer<typeof SCProjectsByVersionSchema>;
