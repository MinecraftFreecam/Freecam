import { z } from "zod";

export const MetadataSchema = z.object({
  mod: z.object({
    version: z.string().min(1),
  }),
});

export type MetadataFile = z.infer<typeof MetadataSchema>;
